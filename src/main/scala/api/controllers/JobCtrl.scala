package com.ilovedatajjia
package api.controllers

import api.dto.input.FileImportOptDtoIn
import api.dto.output.DataPreviewDtoOut
import api.models.SessionMod
import api.services.JobSvc
import cats.data.EitherT
import cats.effect.IO
import fs2.Stream
import io.circe.Json

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
   *   Number of rows of the preview
   * @param minColIdx
   *   Included border minimum index column (Starts from 1)
   * @param maxColIdx
   *   Included border maximum index column (`-1` for all on the right)
   * @return
   *   Data preview OR error
   */
  def computePreview(validatedSession: SessionMod,
                     fileImportOpt: Json,
                     fileImport: Stream[IO, Byte],
                     nbRows: Int,
                     minColIdx: Int,
                     maxColIdx: Int): EitherT[IO, Throwable, DataPreviewDtoOut] = for {
    fileImportOpt <- IO(fileImportOpt.as[FileImportOptDtoIn].left.map(throw _))
    _             <- IO {
                       case _ if (-1 <= nbRows) && (minColIdx == 0)                             => Right()
                       case _ if (-1 <= nbRows) && (1 <= minColIdx) && (minColIdx <= maxColIdx) => Right()
                       case _ if (-1 <= nbRows) && (1 <= minColIdx) && (maxColIdx == -1)        => Right()
                       case _                                                                   =>
                         Left(throw new UnsupportedOperationException("Please make sure query parameters are coherent"))
                     }
    _             <- JobSvc.computePreview()
  } yield ???

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
                                  throw new UnsupportedOperationException("Please make sure your operations start with a read only")
                              }
      inputFittedDf        <- sparkArgsParsed.tail.foldLeftM(inputDf) {
                                case (output, sparkArgC: SparkArgC) => sparkArgC.fitArg(output)
                                case _                              =>
                                  throw new UnsupportedOperationException(
                                    "Please make sure your operations follow with computes only")
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
