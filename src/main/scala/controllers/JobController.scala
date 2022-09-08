package com.ilovedatajjia
package controllers

import cats.effect.IO
import io.circe.Json
import models.Session

/**
 * Controller for jobs logic.
 */
object JobController {

  /**
   * Compute the DataFrame preview of the file using the json parameters.
   * @param validatedSession
   *   Validated session
   * @param jsonParams
   *   Json parameters
   * @param file
   *   String representation of the file
   * @return
   *   DataFrame
   */
  def computePreview(validatedSession: Session, jsonParams: Json, file: String): IO[Json] = {
    ???
  }

}
