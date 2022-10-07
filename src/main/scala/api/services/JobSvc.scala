package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn
import api.dto.output.DataPreviewDtoOut
import api.helpers.NormTypeEnum._
import cats.data.EitherT
import cats.effect.IO
import com.ilovedatajjia.api.dto.input.FileImportOptDtoIn._
import com.ilovedatajjia.api.models.SessionMod
import com.ilovedatajjia.config.SparkServer.spark
import fs2.Stream
import fs2.text
import io.circe.Json
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Dataset

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
   *   Spark [[DataFrame]] OR error
   */
  def readStream(fileImportOptDtoIn: FileImportOptDtoIn,
                 fileImport: Stream[IO, Byte],
                 nbRows: Int): EitherT[IO, Throwable, DataFrame] = fileImportOptDtoIn match {
    case opts: CsvImportOptDtoIn  =>
      // Build read options (CSV file "multiLine" -> "true" is not supported)
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

      // Start the read
      import spark.implicits._
      for {
        fileDrained <- fileImport
                         .through(text.utf8.decode)
                         .through(text.lines)
                         .take(nbRows)
                         .compile
                         .toList
        inputDS      = spark.createDataset(fileDrained)
        output       = spark.read.options(readOptions).csv(inputDS).withNormTypes
      } yield output
    case opts: JsonImportOptDtoIn => ???
  }

}
