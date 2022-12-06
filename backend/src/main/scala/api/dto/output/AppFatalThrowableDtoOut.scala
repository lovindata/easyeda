package com.ilovedatajjia
package api.dto.output

import cats.effect.IO
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * Detected fatal throwable for alerting it could affect server integrity.
 * @param unhandledException
 *   Defined if covering an actual handled exception
 * @param msgServer
 *   HTTP server message to send to client
 */
case class AppFatalThrowableDtoOut(unhandledException: Throwable,
                                   private val msgServer: String =
                                     "Fatal non recoverable throwable detected please restart server if necessary")

/**
 * Companion of [[AppFatalThrowableDtoOut]].
 */
object AppFatalThrowableDtoOut {

  // JSON (de)serializers
  implicit val encThrowable: Encoder[Throwable]      = Encoder.instance(x => Json.fromString(x.toString))
  implicit val decThrowable: Decoder[Throwable]      = Decoder.instance(_.as[String].map(x => new Throwable(x)))
  implicit val enc: Encoder[AppFatalThrowableDtoOut] = deriveEncoder
  implicit val dec: Decoder[AppFatalThrowableDtoOut] = deriveDecoder

  // Entity encoder
  implicit val encEnt: EntityEncoder[IO, AppFatalThrowableDtoOut] = jsonEncoderOf[IO, AppFatalThrowableDtoOut]

}
