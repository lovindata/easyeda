package com.ilovedatajjia
package api.controllers

import api.helpers.CatsEffectExtension.RichArray
import api.models.SessionMod
import api.models.SparkArg
import api.models.SparkArg._
import api.models.SparkOpMod
import api.routes.entities.DataPreviewEnt
import api.routes.entities.DataPreviewEnt.DataSchema
import cats.effect.IO
import io.circe.Json

/**
 * Controller for jobs logic.
 */
object JobCtrl {

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
  def computePreview(validatedSession: SessionMod,
                     sparkArgs: Json,
                     fileStr: String,
                     nbRows: Int,
                     nbCols: Int): IO[DataPreviewEnt] = {
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
    } yield DataPreviewEnt(prevSch.map { case (colName, colType) => DataSchema(colName, colType) }, prevValues)
  }

}
