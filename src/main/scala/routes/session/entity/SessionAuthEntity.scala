package com.ilovedatajjia
package routes.session.entity

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
case class SessionAuthEntity(id: Long, authToken: String)

/**
 * [[SessionAuthEntity]] companion object with encoders & decoders.
 */
object SessionAuthEntity {

  // JSON encoders & decoders
  implicit val jsonEncoder: Encoder[SessionAuthEntity] = deriveEncoder
  implicit val jsonDecoder: Decoder[SessionAuthEntity] = deriveDecoder

  // Entity encoder
  implicit val sessionEntityEncoder: EntityEncoder[IO, SessionAuthEntity] =
    jsonEncoderOf[IO, SessionAuthEntity]

}
