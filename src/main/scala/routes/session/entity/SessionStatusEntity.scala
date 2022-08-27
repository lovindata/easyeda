package com.ilovedatajjia
package routes.session.entity

import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto._
import java.util.UUID
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
case class SessionStatusEntity(id: UUID, createdAt: String, updatedAt: String, terminatedAt: Option[String])

/**
 * [[SessionAuthEntity]] companion object with encoders & decoders.
 */
object SessionStatusEntity {

  // JSON encoders
  implicit val jsonEncoder: Encoder[SessionStatusEntity] = deriveEncoder
  implicit val jsonDecoder: Decoder[SessionStatusEntity] = deriveDecoder

  // Entity encoders
  implicit val entityEncoder: EntityEncoder[IO, SessionStatusEntity]             =
    jsonEncoderOf[IO, SessionStatusEntity]
  implicit val arrayEntityEncoder: EntityEncoder[IO, Array[SessionStatusEntity]] =
    jsonEncoderOf[IO, Array[SessionStatusEntity]]

}
