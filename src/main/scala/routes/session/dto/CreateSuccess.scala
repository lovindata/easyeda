package com.ilovedatajjia
package routes.session.dto

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * JSON response for success session creation.
 * @param msg
 *   Message holder
 * @param authToken
 *   Authentication token of the created session
 */
case class CreateSuccess(msg: String = "Successfully created session.", authToken: String)

/**
 * Holding encoder(s).
 */
object CreateSuccess {

  // For automatic class / JSON encoder
  implicit val successEntityEncoder: EntityEncoder[IO, CreateSuccess] =
    jsonEncoderOf[IO, CreateSuccess] // useful when sending response

}
