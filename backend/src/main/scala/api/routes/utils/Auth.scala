package com.ilovedatajjia
package api.routes.utils

import api.helpers.AppException
import api.helpers.AppException._
import scala.concurrent.Future
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint

/**
 * Authorization related utils.
 */
object Auth {

  // Tapir partial endpoint for authorization
  val authEndpoint: PartialServerEndpoint[String, Unit, Unit, AppException, Unit, Any, Future] = endpoint
    .securityIn(auth.bearer[String]())
    .errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[AppException]))
    .serverSecurityLogic { ??? }

}
