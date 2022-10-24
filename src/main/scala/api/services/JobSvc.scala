package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn
import api.dto.input.FileImportOptDtoIn._
import api.dto.output.DataPreviewDtoOut
import api.dto.output.DataPreviewDtoOut._
import api.helpers.AppLayerException
import api.helpers.AppLayerException.ServiceLayerException
import api.helpers.CatsEffectExtension._
import api.helpers.NormTypeEnum._
import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import fs2.Stream
import fs2.text
import io.circe.fs2.byteArrayParser
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.http4s.Status
import scala.collection.mutable
import scala.concurrent.duration._
import scala.reflect.ClassTag

/**
 * Service layer for jobs.
 */
object JobSvc {

  /**
   * Read the stream according the csv options.
   * @param opts
   *   Csv options
   * @param fileImport
   *   Streamed file binaries
   * @param nbRowsPreviewValidated
   *   Number of rows wanted for the preview (`-1` means read all stream otherwise `nbRowsPreviewValidated` > 0)
   * @return
   *   Spark [[DataFrame]] OR
   *   - [[ServiceLayerException]] if pooling [[Stream]] failed
   *   - [[ServiceLayerException]] if building [[DataFrame]] failed
   */
  private def readStreamCsv(opts: CsvImportOptDtoIn, fileImport: Stream[IO, Byte], nbRowsPreviewValidated: Int)(implicit
      spark: SparkSession): EitherT[IO, AppLayerException, DataFrame] = for {
    // Drain to stream
    fileDrained <- ((opts.header, nbRowsPreviewValidated) match {
                     case (_, -1)    => // `-1` -> all lines
                       fileImport.through(text.utf8.decode).through(text.lines)
                     case (false, _) => // else no header -> take
                       fileImport.through(text.utf8.decode).through(text.lines).take(nbRowsPreviewValidated)
                     case (true, _)  => // else header -> take + 1
                       fileImport.through(text.utf8.decode).through(text.lines).take(nbRowsPreviewValidated + 1)
                   }).compile.toList.attemptE.leftMap(x =>
                     ServiceLayerException(
                       "Not compilable stream or issue occurring when pooling the file binaries in RAM",
                       Some(x),
                       Status.UnprocessableEntity))

    // Build the DataFrame
    output      <- IO.interruptibleMany {
                     val readOptions: Map[String, String] = Map(
                       // Default options
                       "mode"               -> "FAILFAST",
                       "dateFormat"         -> "yyyy-MM-dd",
                       "timestampNTZFormat" -> "yyyy-MM-dd HH:mm:ss.SSSSSS",
                       // Parsed options
                       "sep"                -> opts.sep,
                       "quote"              -> opts.quote,
                       "escape"             -> opts.escape,
                       "header"             -> opts.header.toString,
                       "inferSchema"        -> opts.inferSchema.toString
                     )
                     import spark.implicits._
                     val inputDS: Dataset[String]         = spark.createDataset(fileDrained)
                     spark.read.options(readOptions).csv(inputDS).withNormTypes
                   }.attemptE
                     .leftMap[AppLayerException](x =>
                       ServiceLayerException("Not processable csv file into DataFrame", Some(x), Status.UnprocessableEntity))
  } yield output

  /**
   * Read the stream according the json options.
   * @param opts
   *   Json options
   * @param fileImport
   *   Streamed file binaries
   * @param nbRowsPreviewValidated
   *   Number of rows wanted for the preview (`-1` means read all stream otherwise `nbRowsPreviewValidated` > 0)
   * @return
   *   Spark [[DataFrame]] OR
   *   - [[ServiceLayerException]] if pooling [[Stream]] failed
   *   - [[ServiceLayerException]] if building [[DataFrame]] failed
   */
  private def readStreamJson(opts: JsonImportOptDtoIn, fileImport: Stream[IO, Byte], nbRowsPreviewValidated: Int)(
      implicit spark: SparkSession): EitherT[IO, AppLayerException, DataFrame] = for {
    // Drain to stream
    fileDrained <- (if (nbRowsPreviewValidated > 0) fileImport.through(byteArrayParser).take(nbRowsPreviewValidated)
                    else fileImport.through(byteArrayParser)).compile.toList.attemptE.leftMap(x =>
                     ServiceLayerException(
                       "Not compilable stream or issue occurring when pooling the file binaries in RAM",
                       Some(x),
                       Status.UnprocessableEntity))

    // Build the DataFrame
    output      <- IO.interruptibleMany {
                     val readOptions: Map[String, String] = Map(
                       // Default options
                       "mode"               -> "FAILFAST",
                       "dateFormat"         -> "yyyy-MM-dd",
                       "timestampNTZFormat" -> "yyyy-MM-dd HH:mm:ss.SSSSSS",
                       // Parsed options
                       "primitivesAsString" -> (!opts.inferSchema).toString
                     )
                     import spark.implicits._
                     val inputDS: Dataset[String]         = spark.createDataset(fileDrained.map(_.noSpaces))
                     spark.read.options(readOptions).json(inputDS).withNormTypes
                   }.attemptE
                     .leftMap[AppLayerException](x =>
                       ServiceLayerException("Not processable json file into DataFrame",
                                             Some(x),
                                             Status.UnprocessableEntity))
  } yield output

  /**
   * Read the stream according the file options.
   * @param fileImportOptDtoIn
   *   File options
   * @param fileImport
   *   Streamed file binaries
   * @param nbRowsPreviewValidated
   *   Number of rows wanted for the preview (`-1` means read all stream otherwise `nbRowsPreviewValidated` > 0)
   * @return
   *   Spark [[DataFrame]] OR
   *   - [[ServiceLayerException]] if timed out computation
   *   - exception from [[readStreamCsv]]
   *   - exception from [[readStreamJson]]
   */
  def readStream(fileImportOptDtoIn: FileImportOptDtoIn,
                 fileImport: Stream[IO, Byte],
                 nbRowsPreviewValidated: Int,
                 timeout: Option[FiniteDuration] = Some(10.seconds))(implicit
      spark: SparkSession): EitherT[IO, AppLayerException, DataFrame] = (fileImportOptDtoIn match {
    // 0 - If CSV file
    case opts: CsvImportOptDtoIn  => readStreamCsv(opts, fileImport, nbRowsPreviewValidated)
    // 1 - If JSON file
    case opts: JsonImportOptDtoIn => readStreamJson(opts, fileImport, nbRowsPreviewValidated)
    // 2 - Unknown options
    case _                        => EitherT.left[DataFrame](IO(ServiceLayerException("Unknown matching type for `fileImportOptDtoIn`")))
  }).timeoutOptionTo(
    timeout,
    ServiceLayerException(s"Exceeded timeout `$timeout` when reading stream, data or options need to be restrained",
                          statusCodeServer = Status.BadRequest)
  )

  /**
   * Compute preview of an input [[DataFrame]].
   * @param input
   *   The DataFrame
   * @param nbRowsValidated
   *   Number of rows in the preview (`-1` for all rows)
   * @param minColIdxValidated
   *   Included border minimum index column (Starts from `1` or equal `-1` for no columns)
   * @param maxColIdxValidated
   *   Included border maximum index column (`-1` for all on the right)
   * @param timeout
   *   Preview computation timeout
   * @return
   *   Data preview OR
   *   - [[ServiceLayerException]] if issue on computing DataFrame preview or schema
   *   - [[ServiceLayerException]] if timed out computation
   */
  def preview(input: DataFrame,
              nbRowsValidated: Int,
              minColIdxValidated: Int,
              maxColIdxValidated: Int,
              timeout: Option[FiniteDuration]): EitherT[IO, AppLayerException, DataPreviewDtoOut] = (for {
    // Intermediate result (DataFrame to compute and scalaSchema)
    intermediateOut           <- IO {
                                   // Column slicer
                                   def colPrevSlicer[A: ClassTag](x: List[A]): List[A] =
                                     (minColIdxValidated, maxColIdxValidated) match {
                                       case (-1, _)                           => List()
                                       case (1, -1)                           => x
                                       case (_, -1) if minColIdxValidated > 1 => x.drop(minColIdxValidated - 1)
                                       case _                                 => x.slice(minColIdxValidated - 1, maxColIdxValidated)
                                     }

                                   // Retrieve the sample schema
                                   val scalaSchema: List[(String, NormType)] =
                                     colPrevSlicer(input.schema.fields.toList).map { case StructField(name, dataType, _, _) =>
                                       (name, dataType.toNormType)
                                     }

                                   // Prepare the Spark DAG for sampling values
                                   val inputColsToAllString: List[Column] =
                                     colPrevSlicer(input.columns.toList).map(colName => col(colName).cast(StringType).as(colName))
                                   val inputAllString: DataFrame          =
                                     input.select(inputColsToAllString: _*).na.fill("") // To handle "null" values
                                   val inputColForValues: Column =
                                     array(inputAllString.columns.map(col): _*) as "_$VALUES_"
                                   val inputValues: DataFrame    = inputAllString.select(
                                     inputColForValues
                                   ) // 1 Column where each row "i" is in format "[<_c0_vi>, ..., <_cj_vi>, ..., <_cn_vi>]"

                                   // Return
                                   (inputValues, scalaSchema)
                                 }.attemptE.leftMap(x =>
                                   ServiceLayerException("Not compilable built DataFrame to start computing preview or schema",
                                                         Some(x),
                                                         Status.UnprocessableEntity))
    (inputValues, scalaSchema) = intermediateOut

    // Retrieve the sample values
    rowValues   <- EitherT.right(IO.blocking { // Blocking operation on Spark
                     (if (nbRowsValidated == -1) inputValues.collect()
                      else inputValues.head(nbRowsValidated)).toList
                   })
    scalaValues <- EitherT.right[AppLayerException](
                     IO(
                       rowValues
                         .map(
                           _.getAs[mutable.ArraySeq[String]]("_$VALUES_")
                         ) // ArrayType(StringType) == ArraySeq[String]
                         .map(_.toList)))
  } yield DataPreviewDtoOut(DataConf(scalaValues.length, scalaSchema.length),
                            scalaSchema.map(DataSchema.tupled),
                            scalaValues)).timeoutOptionTo(
    timeout,
    ServiceLayerException(s"Exceeded timeout `$timeout` when computing preview, data or options need to be restrained",
                          statusCodeServer = Status.BadRequest)
  )

}
