package com.ilovedatajjia
package api.dto.output

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
case class SessionAuthDtoOut(id: Long, authToken: String)

/**
 * [[DataPreviewDtoOut]] companion object with encoders & decoders.
 */
object SessionAuthDtoOut {

  // JSON (de)serializers
  implicit val jsonEncoder: Encoder[SessionAuthDtoOut] = deriveEncoder
  implicit val jsonDecoder: Decoder[SessionAuthDtoOut] = deriveDecoder

  // Entity encoder
  implicit val sessionEntityEncoder: EntityEncoder[IO, SessionAuthDtoOut] = jsonEncoderOf[IO, SessionAuthDtoOut]

}
