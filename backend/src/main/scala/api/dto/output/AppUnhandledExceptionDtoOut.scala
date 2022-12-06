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
case class AppUnhandledExceptionDtoOut(unhandledException: Exception,
                                       private val msgServer: String =
                                         "Not handled exception at the moment it will be in next releases")

/**
 * Companion of [[AppUnhandledExceptionDtoOut]].
 */
object AppUnhandledExceptionDtoOut {

  // JSON (de)serializers
  implicit val encThrowable: Encoder[Exception]          = Encoder.instance(x => Json.fromString(x.toString))
  implicit val decThrowable: Decoder[Exception]          = Decoder.instance(_.as[String].map(x => new Exception(x)))
  implicit val enc: Encoder[AppUnhandledExceptionDtoOut] = deriveEncoder
  implicit val dec: Decoder[AppUnhandledExceptionDtoOut] = deriveDecoder

  // Entity encoder
  implicit val encEnt: EntityEncoder[IO, AppUnhandledExceptionDtoOut] = jsonEncoderOf[IO, AppUnhandledExceptionDtoOut]

}
