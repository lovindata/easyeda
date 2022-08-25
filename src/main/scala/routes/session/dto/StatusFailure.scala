package com.ilovedatajjia
package routes.session.dto

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * JSON response for failure session creation.
 * @param msg
 *   Message holder
 */
case class StatusFailure(msg: String, errorMessage: String)

/**
 * Holding encoder(s).
 */
object StatusFailure {

  // For automatic class / JSON encoder
  implicit def failureEntityEncoder: EntityEncoder[IO, CreateFailure] =
    jsonEncoderOf[IO, CreateFailure] // useful when sending response

}
