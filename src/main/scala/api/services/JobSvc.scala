package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn
import api.dto.output.DataPreviewDtoOut
import cats.data.EitherT
import cats.effect.IO
import com.ilovedatajjia.api.dto.input.FileImportOptDtoIn._
import com.ilovedatajjia.api.models.SessionMod
import fs2.Stream
import fs2.text
import io.circe.Json
import org.apache.spark.sql.DataFrame

object JobSvc {

  /**
   * Read the stream according the file options.
   *
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
                 fileImport: Stream[IO, Byte], nbRows: Int): EitherT[IO, Throwable, DataFrame] = fileImportOptDtoIn match {
    case opts: CsvImportOptDtoIn  => fileImport.through(text.utf8.decode).takeWhile()
    case opts: JsonImportOptDtoIn => ???
  }

}
