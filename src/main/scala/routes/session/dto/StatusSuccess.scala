package com.ilovedatajjia
package routes.session.dto

import cats.effect.IO
import io.circe.Encoder
import io.circe.Json
import io.circe.generic.auto._
import models.Session
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * JSON response for success session creation.
 * @param msg
 *   Message holder
 * @param session
 *   Authentication token of the created session
 */
case class StatusSuccess(msg: String = "Successfully retrieved session.", session: Session)

/**
 * Holding encoder(s).
 */
object StatusSuccess {

  // Session encoders
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
  implicit val sessionEntityEncoder: EntityEncoder[IO, Session] =
    jsonEncoderOf[IO, Session] // useful when sending response

  // StatusSuccess entity encoder
  implicit val successEntityEncoder: EntityEncoder[IO, StatusSuccess] =
    jsonEncoderOf[IO, StatusSuccess] // useful when sending response

}
