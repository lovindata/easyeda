package com.ilovedatajjia
package api.controllers

import api.dto.input.FileImportOptDtoIn
import api.dto.input.FileImportOptDtoIn._
import api.dto.output.DataPreviewDtoOut
import api.models.SessionMod
import api.services.JobSvc
import cats.data.EitherT
import cats.effect.IO
import config.SparkServer.spark
import fs2.Stream
import io.circe.Json
import scala.concurrent.duration._

/**
 * Controller for jobs logic.
 */
object JobCtrl {

  /**
   * Validated the parameters and compute the preview.
   * @param validatedSession
   *   Validated session
   * @param fileImportOpt
   *   File options to validate
   * @param fileImport
   *   Ready to be drained stream corresponding the file data
   * @param nbRows
   *   Number of rows in the preview (`-1` for all rows)
   * @param minColIdx
   *   Included border minimum index column (Starts from `1` or equal `-1` for no columns)
   * @param maxColIdx
   *   Included border maximum index column (Higher or equal than `minColIdx` or `-1`)
   * @return
   *   Data preview
   */
  def computePreview(validatedSession: SessionMod,
                     fileImportOpt: Json,
                     fileImport: Stream[IO, Byte],
                     nbRows: Int,
                     minColIdx: Int,
                     maxColIdx: Int): EitherT[IO, Throwable, DataPreviewDtoOut] =
    for {
      // Validations
      _             <- EitherT(
                         IO(
                           if ((-1 <= nbRows) && (minColIdx == 0)) Right(())
                           else if ((-1 <= nbRows) && (1 <= minColIdx) && (minColIdx <= maxColIdx)) Right(())
                           else if ((-1 <= nbRows) && (1 <= minColIdx) && (maxColIdx == -1)) Right(())
                           else Left(new UnsupportedOperationException("Please ensure query parameters are coherent"))
                         ))
      fileImportOpt <- EitherT(IO(fileImportOpt.as[FileImportOptDtoIn]))

      // Computations
      fileImportDataFrame <- JobSvc.readStream(fileImportOpt, fileImport, nbRows)
      dataPreview         <- JobSvc.preview(fileImportDataFrame, nbRows, minColIdx, maxColIdx, Some(10.seconds))
    } yield dataPreview

}
