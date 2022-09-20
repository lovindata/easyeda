package com.ilovedatajjia
package controllers

import cats.effect.IO
import io.circe.Json
import models.job.Job
import models.job.Job.JobType._
import models.operation.SparkArg._
import models.operation.SparkOp
import models.session.Session
import utils.CatsEffectExtension.RichArray

/**
 * Controller for jobs logic.
 */
object JobController {

  /**
   * Compute the DataFrame preview of the file using the json operations.
   * @param validatedSession
   *   Validated session
   * @param sparkArgsDrained
   *   Operations in JSON
   * @param fileStrDrained
   *   String representation of the file
   * @return
   *   DataFrame in JSON
   */
  def computePreview(validatedSession: Session, sparkArgsDrained: IO[Json], fileStrDrained: IO[String]): IO[Json] =
    for {
      // Get file parameters & content
      sparkArgs <- sparkArgsDrained
      fileStr   <- fileStrDrained

      // Starting preview job
      // job            <- Job(validatedSession.id, Preview)
      sparkArgsParsed     = sparkArgs.toSparkArgs
      // _              <- sparkArgsParsed.zipWithIndex.traverse { case (sparkArg, opIdx) => SparkOp(job.id, opIdx, sparkArg) }

      // Run preview Job
      // _                  <- job.toRunning
      inputDf            <- sparkArgsParsed.head match {
                              case x: SparkArgR => x.fitArg(fileStr)
                              case _            =>
                                throw new UnsupportedOperationException("Please make sure your operations start with a read")
                            }
      inputFittedDf      <- sparkArgsParsed.tail.foldLeftM(inputDf) { case (output, sparkArgC: SparkArgC) =>
                              sparkArgC.fitArg(output)
                            }
      outputStartCompute <- SparkOp.preview(inputFittedDf)

      // Terminate job
      // _ <- job.toTerminated
    } yield outputStartCompute

}
