package com.ilovedatajjia
package api.routes

import api.controllers.ConnCtrl
import api.dto.input.ConnFormDtoIn
import api.dto.input.ConnFormDtoIn._
import api.dto.output.ConnTestDtoOut
import api.dto.output.ConnTestDtoOut._
import api.helpers.AppException._
import cats.effect.IO
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.AnyEndpoint
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for connections management.
 */
object ConnRts extends GenericRts {

  // Test connection
  private val testEpt                 = authEpt
    .summary("test unknown connection")
    .post
    .in("conn" / "test")
    .in(jsonBody[ConnFormDtoIn])
    .out(jsonBody[ConnTestDtoOut])
  private val testRts: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(testEpt.serverLogic(_ => ConnCtrl.testConn(_).toErrHandled))

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(testEpt).map(_.endpoint)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = testRts
}
