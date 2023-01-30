package com.ilovedatajjia
package api.routes

import api.helpers.AppException
import api.helpers.AppException._
import api.models.UserMod
import api.services.UserSvc
import cats.effect.IO
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint

/**
 * Authorization related utils.
 */
trait GenericRts {

  // Error handled endpoint
  val errHandledEpt: Endpoint[Unit, Unit, AppException, Unit, Any] =
    endpoint.errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[AppException]))

  // Authorization handled endpoint
  val authEpt: PartialServerEndpoint[String, UserMod, Unit, AppException, Unit, Any, IO] = errHandledEpt
    .securityIn(auth.bearer[String]())
    .serverSecurityLogic { UserSvc.grantAccess(_).toErrHandled }

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  def docEpt: List[AnyEndpoint]

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  def appRts: HttpRoutes[IO]

}
