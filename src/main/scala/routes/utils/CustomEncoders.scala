package com.ilovedatajjia
package routes.utils

import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.generic.semiauto._
import models.Session
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * Custom class [[EntityEncoder]] for responses.
 */
object CustomEncoders {

  // Session JSON encoders
  implicit val sessionEncoder: Encoder[Session]                 = (session: Session) =>
    Json.obj(
      ("id", Json.fromString(session.id.toString)),
      ("createdAt", Json.fromString(session.getCreatedAt.toString)),
      ("updatedAt", Json.fromString(session.getUpdatedAt.toString)),
      ("terminatedAt",
       session.getTerminatedAt match {
         case None               => Json.Null
         case Some(terminatedAt) => Json.fromString(terminatedAt.toString)
       })
    )
  implicit val sessionDecoder: Decoder[Session]                 = deriveDecoder
  implicit val sessionEntityEncoder: EntityEncoder[IO, Session] =
    jsonEncoderOf[IO, Session] // useful when sending response

}
