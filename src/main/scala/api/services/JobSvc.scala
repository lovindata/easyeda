package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn
import api.dto.input.FileImportOptDtoIn._
import api.dto.output.AppLayerExceptionDtoOut
import api.dto.output.DataPreviewDtoOut
import api.dto.output.DataPreviewDtoOut._
import api.helpers.AppLayerException
import api.helpers.AppLayerException.ServiceLayerException
import api.helpers.CatsEffectExtension._
import api.helpers.NormTypeEnum._
import api.models.JobMod
import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import fs2.Stream
import fs2.text
import io.circe.Encoder
import io.circe.Json
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
   * Wrapper for auto-starting & auto-closing a [[JobMod]] state. ([[EitherT]] version)
   * @param sessionId
   *   Session ID
   * @param inputs
   *   JSON representation of the input(s)
   * @param timeout
   *   Job computation timeout
   * @param f
   *   Wrapped function
   * @tparam A
   *   Output type
   * @return
   *   Output from wrapped function
   */
  def withJob[A](sessionId: Long, inputs: Json, timeout: Option[FiniteDuration])(
      f: EitherT[IO, AppLayerException, A])(implicit
      encOutFailed: Encoder[AppLayerExceptionDtoOut],
      encOutSucceeded: Encoder[A]): EitherT[IO, AppLayerException, A] = EitherT(for {
    jobInRunning <- JobMod(sessionId, inputs)
    output       <- timeout match {
                      case None          => f.value
                      case Some(timeout) =>
                        f.value.timeoutTo(
                          timeout,
                          IO(Left(ServiceLayerException(
                            s"Exceeded timeout `$timeout` for job `${jobInRunning.id}`, data or options need to be restrained",
                            statusCodeServer = Status.BadRequest)))
                        )
                    }
    outputJson    = output.bimap(x => encOutFailed(x.toDtoOut), x => encOutSucceeded(x))
    _            <- jobInRunning.terminate(outputJson).value
  } yield output)

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
                       Status.UnprocessableEntity,
                       Some(x)))

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
                     .leftMap(x =>
                       ServiceLayerException("Not processable csv file into DataFrame", Status.UnprocessableEntity, Some(x)))
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
                       Status.UnprocessableEntity,
                       Some(x)))

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
                                             Status.UnprocessableEntity,
                                             Some(x)))
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
   *   - [[ServiceLayerException]] if unknown file options
   *   - exception from [[readStreamCsv]]
   *   - exception from [[readStreamJson]]
   */
  def readStream(fileImportOptDtoIn: FileImportOptDtoIn, fileImport: Stream[IO, Byte], nbRowsPreviewValidated: Int)(
      implicit spark: SparkSession): EitherT[IO, AppLayerException, DataFrame] = fileImportOptDtoIn match {
    // 0 - If CSV file
    case opts: CsvImportOptDtoIn  => readStreamCsv(opts, fileImport, nbRowsPreviewValidated)
    // 1 - If JSON file
    case opts: JsonImportOptDtoIn => readStreamJson(opts, fileImport, nbRowsPreviewValidated)
    // 2 - Unknown options
    case _                        =>
      EitherT.left[DataFrame](
        IO(
          ServiceLayerException("File options correctly received but will handled in the next version",
                                Status.BadRequest)))
  }

  /**
   * Apply the custom schema on an input [[DataFrame]]. (Do nothing in case of non define custom schema)
   * @param input
   *   Spark input [[DataFrame]]
   * @param customSchema
   *   Custom schema to apply
   * @return
   *   Spark [[DataFrame]] with the custom schema applied OR
   *   - [[ServiceLayerException]] if out of bound natural index
   *   - [[ServiceLayerException]] if incoherent custom type on a column
   */
  private def applyCustomSchema(input: DataFrame,
                                customSchema: List[CustomColSchema]): EitherT[IO, AppLayerException, DataFrame] =
    for {
      // Replace natural index by actual names in the selector
      natIdxRep    <- EitherT(IO {
                        val inputColNames: Array[String] = input.columns
                        val nbCols: Int                  = input.columns.length
                        customSchema.traverse { // Using `_.traverse` for first failed
                          case CustomColSchema(natIdx, normType, newColName) if natIdx < nbCols =>
                            Right(inputColNames(natIdx), normType, newColName)
                          case CustomColSchema(natIdx, _, _)                                    =>
                            Left(
                              ServiceLayerException(s"Out of bound column index `$natIdx` not in `[1 $nbCols]`",
                                                    Status.UnprocessableEntity))
                        }
                      })

      // Apply the column types & new names in the selector
      inputOutCols <-
        EitherT(IO {
          natIdxRep.traverse { // Using `_.traverse` for first failed
            case (natColName, Some(CustomColBase(newColType)), optionalNewColName)                   =>
              Right(
                input(natColName)
                  .cast(newColType.toDataType)
                  .as(optionalNewColName.getOrElse(natColName)))
            case (natColName, Some(CustomColDate(Date, fmtDate)), optionalNewColName)                =>
              Right(to_date(input(natColName), fmtDate).as(optionalNewColName.getOrElse(natColName)))
            case (natColName, Some(CustomColTimestamp(Timestamp, fmtTimestamp)), optionalNewColName) =>
              Right(to_timestamp(input(natColName), fmtTimestamp).as(optionalNewColName.getOrElse(natColName)))
            case (natColName, None, optionalNewColName)                                              =>
              Right(input(natColName).as(optionalNewColName.getOrElse(natColName)))
            case (natColName, _, _)                                                                  =>
              Left(
                ServiceLayerException(s"Incoherent defined custom type for the column `$natColName`",
                                      Status.UnprocessableEntity))
          }
        })
    } yield input.select(inputOutCols: _*)

  /**
   * Pass the custom schema step if the file options contains one. (Do nothing otherwise)
   * @param input
   *   Input [[DataFrame]] to pass the custom schema on
   * @param fileImportOptDtoIn
   *   [[FileImportOptDtoIn]] containing a potential custom schema
   * @return
   *   Input [[DataFrame]] with the custom schema step passed OR
   *   - exception from [[applyCustomSchema]]
   */
  def passCustomSchema(input: DataFrame,
                       fileImportOptDtoIn: FileImportOptDtoIn): EitherT[IO, AppLayerException, DataFrame] =
    fileImportOptDtoIn match {
      // Cases to apply custom schema
      case CsvImportOptDtoIn(_, _, _, _, _, Some(customSchema)) => applyCustomSchema(input, customSchema)
      case JsonImportOptDtoIn(_, Some(customSchema))            => applyCustomSchema(input, customSchema)

      // Cases to not apply custom schema
      case _ => EitherT.right(IO(input))
    }

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
   * @return
   *   Data preview OR
   *   - [[ServiceLayerException]] if issue on computing DataFrame preview or schema
   *   - [[ServiceLayerException]] if timed out computation
   */
  def preview(input: DataFrame,
              nbRowsValidated: Int,
              minColIdxValidated: Int,
              maxColIdxValidated: Int): EitherT[IO, AppLayerException, DataPreviewDtoOut] = for {
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
                                                         Status.UnprocessableEntity,
                                                         Some(x)))
    (inputValues, scalaSchema) = intermediateOut

    // Retrieve the sample values
    rowValues   <- EitherT.right(IO.interruptibleMany { // Blocking operation on Spark
                     (if (nbRowsValidated == -1) inputValues.collect()
                      else inputValues.head(nbRowsValidated)).toList
                   })
    scalaValues <-
      EitherT.right[AppLayerException](
        IO(
          rowValues
            .map(_.getAs[mutable.ArraySeq[String]]("_$VALUES_")) // ArrayType(StringType) == ArraySeq[String]
            .map(_.toList)))
  } yield DataPreviewDtoOut(DataConf(scalaValues.length, scalaSchema.length),
                            scalaSchema.map(DataSchema.tupled),
                            scalaValues)

}
