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
 * @param createdAt
 *   Authorization token
 * @param updatedAt
 *   Authorization token
 */
case class SessionStatusDtoOut(id: Long, createdAt: String, updatedAt: String, terminatedAt: Option[String])

/**
 * [[DataPreviewDtoOut]] companion object with encoders & decoders.
 */
object SessionStatusDtoOut {

  // JSON (de)serializers
  implicit val jsonEncoder: Encoder[SessionStatusDtoOut] = deriveEncoder
  implicit val jsonDecoder: Decoder[SessionStatusDtoOut] = deriveDecoder

  // Entity encoders
  implicit val entityEncoder: EntityEncoder[IO, SessionStatusDtoOut]           = jsonEncoderOf[IO, SessionStatusDtoOut]
  implicit val listEntityEncoder: EntityEncoder[IO, List[SessionStatusDtoOut]] =
    jsonEncoderOf[IO, List[SessionStatusDtoOut]]

}
