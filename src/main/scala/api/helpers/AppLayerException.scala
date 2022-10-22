package com.ilovedatajjia
package api.helpers

import api.dto.output.AppLayerExceptionDtoOut
import api.dto.output.AppLayerExceptionDtoOut._
import api.helpers.Http4sExtension._
import cats.effect.IO
import org.http4s.Response
import org.http4s.Status
import org.http4s.dsl.io._

/**
 * Handled application exception happening in the different layers.
 */
sealed trait AppLayerException extends Exception {

  // Mandatory fields
  def msgServer: String
  def overHandledException: Option[Exception]
  def statusCodeServer: Status

  /**
   * Convert to [[AppLayerExceptionDtoOut]].
   * @return
   *   Output DTO
   */
  def toDtoOut: AppLayerExceptionDtoOut

  /**
   * Convert to [[Response]].
   * @return
   *   Http response
   */
  def toResponseIO: IO[Response[IO]] = statusCodeServer.toResponseIOWithDtoOut(toDtoOut)

}

/**
 * ADT of [[AppLayerException]].
 */
object AppLayerException {

  /**
   * [[AppLayerException]] happening in the [[api.routes]] layer.
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   * @param statusCodeServer
   *   HTTP status code to send to client
   */
  case class RouteLayerException(msgServer: String,
                                 overHandledException: Option[Exception] = None,
                                 statusCodeServer: Status = InternalServerError)
      extends AppLayerException {

    /**
     * Convert to [[RouteLayerExceptionDtoOut]].
     */
    override def toDtoOut: RouteLayerExceptionDtoOut =
      RouteLayerExceptionDtoOut(msgServer, overHandledException.map(_.toString))

  }

  /**
   * [[AppLayerException]] happening in the [[api.controllers]] layer.
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   * @param statusCodeServer
   *   HTTP status code to send to client
   */
  case class ControllerLayerException(msgServer: String,
                                      overHandledException: Option[Exception] = None,
                                      statusCodeServer: Status = Status.InternalServerError)
      extends AppLayerException {

    /**
     * Convert to [[ControllerLayerExceptionDtoOut]].
     */
    override def toDtoOut: ControllerLayerExceptionDtoOut =
      ControllerLayerExceptionDtoOut(msgServer, overHandledException.map(_.toString))

  }

  /**
   * [[AppLayerException]] happening in the [[api.services]] layer.
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   * @param statusCodeServer
   *   HTTP status code to send to client
   */
  case class ServiceLayerException(msgServer: String,
                                   overHandledException: Option[Exception] = None,
                                   statusCodeServer: Status = Status.InternalServerError)
      extends AppLayerException {

    /**
     * Convert to [[ServiceLayerExceptionDtoOut]].
     */
    override def toDtoOut: ServiceLayerExceptionDtoOut =
      ServiceLayerExceptionDtoOut(msgServer, overHandledException.map(_.toString))

  }

  /**
   * [[AppLayerException]] happening in the [[api.models]] layer.
   * @param msgServer
   *   HTTP server message to send to client
   * @param overHandledException
   *   Defined if covering an actual handled exception
   * @param statusCodeServer
   *   HTTP status code to send to client
   */
  case class ModelLayerException(msgServer: String,
                                 overHandledException: Option[Exception] = None,
                                 statusCodeServer: Status = Status.InternalServerError)
      extends AppLayerException {

    /**
     * Convert to [[ModelLayerExceptionDtoOut]].
     */
    override def toDtoOut: ModelLayerExceptionDtoOut =
      ModelLayerExceptionDtoOut(msgServer, overHandledException.map(_.toString))

  }

}
