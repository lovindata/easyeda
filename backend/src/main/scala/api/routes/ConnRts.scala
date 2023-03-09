package com.ilovedatajjia
package api.routes

import api.controllers.ConnCtrl
import api.dto.input.ConnFormDtoIn
import api.dto.input.ConnFormDtoIn._
import api.dto.output.ConnStatusDtoOut
import api.dto.output.ConnStatusDtoOut._
import api.dto.output.ConnTestDtoOut
import api.dto.output.ConnTestDtoOut._
import api.helpers.AppException
import api.helpers.AppException._
import api.models.UserMod
import api.services.ConnSvc
import cats.effect.IO
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.AnyEndpoint
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for connections management.
 */
object ConnRts extends GenericRts {

  // Test connection
  private val testEpt: PartialServerEndpoint[String, UserMod, ConnFormDtoIn, AppException, ConnTestDtoOut, Any, IO] =
    authEpt
      .summary("test unknown connection")
      .post
      .in("conn" / "test")
      .in(jsonBody[ConnFormDtoIn])
      .out(jsonBody[ConnTestDtoOut])
  private val testRts: HttpRoutes[IO]                                                                               =
    Http4sServerInterpreter[IO]().toRoutes(testEpt.serverLogic(_ => ConnSvc.testConn(_).toErrHandled))

  // Create connection
  private val createEpt
      : PartialServerEndpoint[String, UserMod, ConnFormDtoIn, AppException, ConnStatusDtoOut, Any, IO] = authEpt
    .summary("create connection")
    .post
    .in("conn" / "create")
    .in(jsonBody[ConnFormDtoIn])
    .out(jsonBody[ConnStatusDtoOut])
  private val createRts: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(createEpt.serverLogic { user => form =>
    ConnCtrl.createConn(user, form).toErrHandled
  })

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(testEpt, createEpt).map(_.endpoint)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = testRts <+> createRts
}
