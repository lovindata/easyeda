package com.ilovedatajjia
package api.dto.output

import cats.effect.IO
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * Detected fatal throwable for alerting it could affect server integrity.
 * @param msgServer
 *   HTTP server message to send to client
 * @param overHandledThrowable
 *   Defined if covering an actual handled exception
 */
case class AppFatalThrowableDtoOut(msgServer: String =
                                     "Fatal non recoverable throwable detected please restart server if necessary",
                                   overHandledThrowable: String)

/**
 * Companion of [[AppFatalThrowableDtoOut]].
 */
object AppFatalThrowableDtoOut {

  // JSON (de)serializers
  implicit val enc: Encoder[AppFatalThrowableDtoOut] = deriveEncoder
  implicit val dec: Decoder[AppFatalThrowableDtoOut] = deriveDecoder

  // Entity encoder
  implicit val encEnt: EntityEncoder[IO, AppFatalThrowableDtoOut] = jsonEncoderOf[IO, AppFatalThrowableDtoOut]

}
