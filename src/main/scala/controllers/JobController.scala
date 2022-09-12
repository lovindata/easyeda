package com.ilovedatajjia
package controllers

import cats.effect.IO
import com.ilovedatajjia.models.session.Session
import io.circe.Json

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

      // Create & Compute preview Job
      job <- Job()

      // Save & Return result

    } yield ???

}
