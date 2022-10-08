package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn
import api.dto.input.FileImportOptDtoIn._
import api.dto.output.DataPreviewDtoOut
import api.dto.output.DataPreviewDtoOut._
import api.helpers.NormTypeEnum._
import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import config.SparkServer.spark
import config.SparkServer.spark.implicits._
import fs2.Stream
import fs2.text
import io.circe.fs2.byteArrayParser
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.collection.mutable
import scala.concurrent.duration._
import scala.reflect.ClassTag

/**
 * Service layer for jobs.
 */
object JobSvc {

  /**
   * Read the stream according the file options.
   * @param fileImportOptDtoIn
   *   File options
   * @param fileImport
   *   File binaries
   * @param nbRows
   *   Number of rows useful in the stream
   * @return
   *   Spark [[DataFrame]]
   */
  def readStream(fileImportOptDtoIn: FileImportOptDtoIn,
                 fileImport: Stream[IO, Byte],
                 nbRows: Int,
                 timeout: FiniteDuration = 10.seconds): EitherT[IO, Throwable, DataFrame] = fileImportOptDtoIn match {
    // 0 - If CSV file
    case opts: CsvImportOptDtoIn  =>
      for {
        fileDrained <- fileImport
                         .through(text.utf8.decode)
                         .through(text.lines)
                         .take(nbRows)
                         .compile
                         .toList
                         .attemptT
        output      <- IO.interruptibleMany {
                         val readOptions: Map[String, String] = Map(
                           // Default options
                           "mode"               -> "FAILFAST",
                           "dateFormat"         -> "yyyy-MM-dd",
                           "timestampNTZFormat" -> "yyyy-MM-dd HH:mm:ss.SSSSSS",
                           // Parsed options
                           "sep"                -> opts.sep.toString,
                           "quote"              -> opts.quote.toString,
                           "escape"             -> opts.escape.toString,
                           "header"             -> opts.header.toString,
                           "inferSchema"        -> opts.inferSchema.toString
                         )
                         val inputDS: Dataset[String]         = spark.createDataset(fileDrained)
                         spark.read.options(readOptions).csv(inputDS).withNormTypes
                       }.timeout(timeout)
                         .attemptT
      } yield output
    // 1 - If JSON file
    case opts: JsonImportOptDtoIn =>
      for {
        fileDrained <- fileImport
                         .through(byteArrayParser)
                         .take(nbRows)
                         .compile
                         .toList
                         .attemptT
        output      <- IO.interruptibleMany {
                         val readOptions: Map[String, String] = Map(
                           // Default options
                           "mode"               -> "FAILFAST",
                           "dateFormat"         -> "yyyy-MM-dd",
                           "timestampNTZFormat" -> "yyyy-MM-dd HH:mm:ss.SSSSSS",
                           // Parsed options
                           "primitivesAsString" -> (!opts.inferSchema).toString
                         )
                         val inputDS: Dataset[String]         = spark.createDataset(fileDrained.map(_.noSpaces))
                         spark.read.options(readOptions).json(inputDS).withNormTypes
                       }.timeout(timeout)
                         .attemptT
      } yield output
    // 2 - Unknown options
    case _                        => EitherT.left(throw new RuntimeException("Unknown matching type for `fileImportOptDtoIn`"))
  }

  /**
   * Compute preview of an input [[DataFrame]].
   * @param input
   *   The DataFrame
   * @param nbRows
   *   Number of rows of the preview
   * @param minColIdx
   *   Included border minimum index column (Starts from `1` or equal `-1` for no columns)
   * @param maxColIdx
   *   Included border maximum index column (`-1` for all on the right)
   * @param timeout
   *   Preview computation timeout
   * @return
   *   Data preview
   */
  def preview(input: DataFrame,
              nbRows: Int,
              minColIdx: Int,
              maxColIdx: Int,
              timeout: FiniteDuration = 10.seconds): EitherT[IO, Throwable, DataPreviewDtoOut] = IO
    .interruptibleMany {
      // Column slicer
      def colPrevSlicer[A: ClassTag](x: Array[A]): Array[A] = (minColIdx, maxColIdx) match {
        case (-1, _) => Array()
        case (_, -1) => x
        case _       => x.slice(minColIdx - 1, maxColIdx - 1)
      }

      // Retrieve the sample schema
      val scalaSchema: Array[(String, NormType)] = colPrevSlicer(input.schema.fields).map {
        case StructField(name, dataType, _, _) => (name, dataType.toNormType)
      }

      // Prepare the Spark DAG for sampling values
      val inputColsToAllString: Array[Column] =
        colPrevSlicer(input.columns).map(colName => col(colName).cast(StringType).as(colName))
      val inputAllString: DataFrame           = input.select(inputColsToAllString: _*).na.fill("") // To handle "null" values
      val inputColForValues: Column           = array(inputAllString.columns.map(col): _*) as "_$VALUES_"
      val inputValues: DataFrame              = inputAllString.select(
        inputColForValues
      ) // 1 Column where each row "i" is in format "[<_c0_vi>, ..., <_cj_vi>, ..., <_cn_vi>]"

      // Retrieve the sample values
      val rowValues: Array[Row]             = inputValues.head(nbRows) // <- Blocking operation
      val scalaValues: Array[Array[String]] = rowValues
        .map(
          _.getAs[mutable.ArraySeq[String]]("_$VALUES_")
        ) // ArrayType(StringType) == ArraySeq[String]
        .map(_.toArray)

      // Return
      DataPreviewDtoOut(DataConf(scalaValues.length, scalaSchema.length),
                        scalaSchema.map(DataSchema.tupled),
                        scalaValues)
    }
    .timeout(timeout)
    .attemptT

}
