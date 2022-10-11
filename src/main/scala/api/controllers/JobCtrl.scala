package com.ilovedatajjia
package api.controllers

import api.dto.input.FileImportOptDtoIn
import api.dto.input.FileImportOptDtoIn._
import api.dto.output.DataPreviewDtoOut
import api.models.SessionMod
import api.services.JobSvc
import cats.data.EitherT
import cats.effect.IO
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

  /*
  /**
   * Compute the DataFrame preview of the file using the json operations.
   * @param validatedSession
   *   Validated session
   * @param sparkArgs
   *   Operations in JSON
   * @param fileStr
   *   String representation of the file
   * @param nbRows
   *   Preview number of rows
   * @param nbCols
   *   Preview number of columns
   * @return
   *   DataFrame preview
   */
  def computePreviewDeprecated(validatedSession: SessionMod,
                               sparkArgs: Json,
                               fileStr: String,
                               nbRows: Int,
                               nbCols: Int): IO[DataPreviewDtoOut] = {
    // Parse operations
    val sparkArgsParsed: Array[SparkArg] = sparkArgs.toSparkArgs

    // Start actual computation logic
    for {
      // Starting preview job
      // _              <- sparkArgsParsed.zipWithIndex.traverse { case (sparkArg, opIdx) => SparkOp(job.id, opIdx, sparkArg) }

      // Run preview Job
      // _                  <- job.toRunning
      inputDf              <- sparkArgsParsed.head match {
                                case x: SparkArgR => x.fitArg(fileStr)
                                case _            =>
                                  throw new UnsupportedOperationException("Please ensure your operations start with a read only")
                              }
      inputFittedDf        <- sparkArgsParsed.tail.foldLeftM(inputDf) {
                                case (output, sparkArgC: SparkArgC) => sparkArgC.fitArg(output)
                                case _                              =>
                                  throw new UnsupportedOperationException(
                                    "Please ensure your operations follow with computes only")
                              }
      prevData             <- SparkOpMod.preview(inputFittedDf, nbRows, nbCols)
      (prevSch, prevValues) = prevData

      // Terminate job
      // _ <- job.toTerminated
    } yield DataPreviewDtoOut(DataConf(nbRows, nbCols),
                              prevSch.map { case (colName, colType) => DataSchema(colName, colType) },
                              prevValues)
  }
   */

}
