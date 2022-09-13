package com.ilovedatajjia
package controllers

import cats.effect.IO
import io.circe.Json
import models.job.FileParams
import models.job.Job
import models.job.Job.JobType._
import models.session.Session

/**
 * Controller for jobs logic.
 */
object JobController {

  /**
   * Compute the DataFrame preview of the file using the json parameters.
   * @param validatedSession
   *   Validated session
   * @param fileParamsDrained
   *   File parameters in JSON
   * @param fileStrDrained
   *   String representation of the file
   * @return
   *   DataFrame in JSON
   */
  def computePreview(validatedSession: Session, fileParamsDrained: IO[Json], fileStrDrained: IO[String]): IO[Json] =
    for {
      // Get file parameters & content
      fileParams <- fileParamsDrained
      fileBytes  <- fileStrDrained

      // Starting preview Job
      job <- Job(validatedSession.id, Preview)
      _   <- fileParams
               .as[FileParams] // TODO in to find a good way to deal with decoding + fileParams insert DB at the same time

      // Save & Return result

    } yield ???

}
