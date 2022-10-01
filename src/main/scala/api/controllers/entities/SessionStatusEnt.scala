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
 * @param createdAt
 *   Authorization token
 * @param updatedAt
 *   Authorization token
 */
case class SessionStatusEnt(id: Long, createdAt: String, updatedAt: String, terminatedAt: Option[String])

/**
 * [[DataPreviewEnt]] companion object with encoders & decoders.
 */
object SessionStatusEnt {

  // JSON encoders
  implicit val jsonEncoder: Encoder[SessionStatusEnt] = deriveEncoder
  implicit val jsonDecoder: Decoder[SessionStatusEnt] = deriveDecoder

  // Entity encoders
  implicit val entityEncoder: EntityEncoder[IO, SessionStatusEnt]             =
    jsonEncoderOf[IO, SessionStatusEnt]
  implicit val arrayEntityEncoder: EntityEncoder[IO, Array[SessionStatusEnt]] =
    jsonEncoderOf[IO, Array[SessionStatusEnt]]

}
