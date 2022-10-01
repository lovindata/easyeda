package com.ilovedatajjia
package api.controllers.entities

import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * For JSON automatic derivation.
 * @param id
 *   Session ID
 * @param authToken
 *   Authorization token
 */
case class SessionAuthEnt(id: Long, authToken: String)

/**
 * [[DataPreviewEnt]] companion object with encoders & decoders.
 */
object SessionAuthEnt {

  // JSON encoders & decoders
  implicit val jsonEncoder: Encoder[SessionAuthEnt] = deriveEncoder
  implicit val jsonDecoder: Decoder[SessionAuthEnt] = deriveDecoder

  // Entity encoder
  implicit val sessionEntityEncoder: EntityEncoder[IO, SessionAuthEnt] = jsonEncoderOf[IO, SessionAuthEnt]

}
