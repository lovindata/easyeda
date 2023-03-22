package com.ilovedatajjia
package api.routes

import api.helpers.BackendException
import api.helpers.BackendException._
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
  val ept: Endpoint[Unit, Unit, BackendException, Unit, Any] =
    endpoint.errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[BackendException]))

  // Authorization handled endpoint
  val authEpt: PartialServerEndpoint[String, UserMod, Unit, BackendException, Unit, Any, IO] = ept
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
