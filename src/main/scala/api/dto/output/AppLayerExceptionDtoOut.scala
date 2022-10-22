package com.ilovedatajjia
package api.dto.output

import api.helpers.AppLayerException
import cats.effect.IO
import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * Mirror DTO of [[AppLayerException]].
 */
sealed trait AppLayerExceptionDtoOut

/**
 * ADT of [[AppLayerExceptionDtoOut]].
 */
object AppLayerExceptionDtoOut {

  // JSON (de)serializers
  implicit val confJson: Configuration               = Configuration.default.withDiscriminator("classOf")
  implicit val enc: Encoder[AppLayerExceptionDtoOut] = deriveConfiguredEncoder[AppLayerExceptionDtoOut]
  implicit val dec: Decoder[AppLayerExceptionDtoOut] = deriveConfiguredDecoder[AppLayerExceptionDtoOut]

  // Entity encoder
  implicit val encEnt: EntityEncoder[IO, AppLayerExceptionDtoOut] = jsonEncoderOf[IO, AppLayerExceptionDtoOut]

  /**
   * Mirror DTO of [[RouteLayerExceptionDtoOut]].
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   */
  case class RouteLayerExceptionDtoOut(msgServer: String, overHandledException: Option[String] = None)
      extends AppLayerExceptionDtoOut

  /**
   * Mirror DTO of [[ControllerLayerExceptionDtoOut]].
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   */
  case class ControllerLayerExceptionDtoOut(msgServer: String, overHandledException: Option[String] = None)
      extends AppLayerExceptionDtoOut

  /**
   * Mirror DTO of [[ServiceLayerExceptionDtoOut]].
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   */
  case class ServiceLayerExceptionDtoOut(msgServer: String, overHandledException: Option[String] = None)
      extends AppLayerExceptionDtoOut

  /**
   * Mirror DTO of [[ModelLayerExceptionDtoOut]].
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   */
  case class ModelLayerExceptionDtoOut(msgServer: String, overHandledException: Option[String] = None)
      extends AppLayerExceptionDtoOut

}
