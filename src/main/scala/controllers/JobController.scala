package com.ilovedatajjia
package controllers

import cats.effect.IO
import io.circe.Json
import models.operation.SparkArg
import models.operation.SparkArg._
import models.operation.SparkOp
import models.session.Session
import org.apache.spark.sql.DataFrame
import utils.CatsEffectExtension.RichArray

/**
 * Controller for jobs logic.
 */
object JobController {

  /**
   * Compute the DataFrame preview of the file using the json operations.
   * @param validatedSession
   *   Validated session
   * @param sparkArgs
   *   Operations in JSON
   * @param fileStr
   *   String representation of the file
   * @return
   *   DataFrame in JSON
   */
  def computePreview(validatedSession: Session, sparkArgs: Json, fileStr: String): IO[Json] = {

    val sparkArgsParsed: Array[SparkArg] = sparkArgs.toSparkArgs

    for {
      // Starting preview job
      _       <- IO.println("#####################################################")
      _       <- IO.println(sparkArgsParsed.mkString("Array(", ",", ")"))
      _       <- IO.println("#####################################################")
      // _              <- sparkArgsParsed.zipWithIndex.traverse { case (sparkArg, opIdx) => SparkOp(job.id, opIdx, sparkArg) }

      // Run preview Job
      // _                  <- job.toRunning
      inputDf <- sparkArgsParsed.head match {
                   case x: SparkArgR => x.fitArg(fileStr)
                   case _            =>
                     throw new UnsupportedOperationException("Please make sure your operations start with a read")
                 }

      _ <- IO.println("#####################################################")
      _ <- IO(inputDf.show(false))
      _ <- IO.println("#####################################################")

      inputFittedDf      <- sparkArgsParsed.tail.foldLeftM(inputDf) { case (output, sparkArgC: SparkArgC) =>
                              sparkArgC.fitArg(output)
                            }
      outputStartCompute <- SparkOp.preview(inputFittedDf)

      // Terminate job
      // _ <- job.toTerminated
    } yield outputStartCompute
  }

}
