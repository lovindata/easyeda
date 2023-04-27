package com.ilovedatajjia
package api.routes

import api.controllers.ConnCtrl
import api.dto.input.ConnFormIDto
import api.dto.input.ConnFormIDto._
import api.dto.output.ConnODto
import api.dto.output.ConnODto._
import api.dto.output.ConnTestODto
import api.dto.output.ConnTestODto._
import api.helpers.BackendException
import api.helpers.BackendException._
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
  private val testEpt: PartialServerEndpoint[String, UserMod, ConnFormIDto, BackendException, ConnTestODto, Any, IO] =
    authEpt
      .summary("test unknown connection")
      .post
      .in("conn" / "test")
      .in(jsonBody[ConnFormIDto])
      .out(jsonBody[ConnTestODto])
  private val testRts: HttpRoutes[IO]                                                                                =
    Http4sServerInterpreter[IO]().toRoutes(testEpt.serverLogic(_ => ConnSvc.testConn(_).toErrHandled))

  // Create connection
  private val createEpt: PartialServerEndpoint[String, UserMod, ConnFormIDto, BackendException, ConnODto, Any, IO] =
    authEpt
      .summary("create connection")
      .post
      .in("conn" / "create")
      .in(jsonBody[ConnFormIDto])
      .out(jsonBody[ConnODto])
  private val createRts: HttpRoutes[IO]                                                                            = Http4sServerInterpreter[IO]().toRoutes(createEpt.serverLogic { user => form =>
    ConnCtrl.createConn(user, form).toErrHandled
  })

  // List connection
  private val listEpt: PartialServerEndpoint[String, UserMod, Unit, BackendException, List[ConnODto], Any, IO] =
    authEpt
      .summary("listing all connections")
      .get
      .in("conn" / "list")
      .out(jsonBody[List[ConnODto]])
  private val listRts: HttpRoutes[IO]                                                                          =
    Http4sServerInterpreter[IO]().toRoutes(listEpt.serverLogic { user => _ => ConnSvc.listConn(user).toErrHandled })

  // Test known connection
  private val testKnownEpt: PartialServerEndpoint[String, UserMod, Long, BackendException, ConnTestODto, Any, IO] =
    authEpt
      .summary("test known connection")
      .post
      .in("conn" / path[Long]("id") / "test")
      .out(jsonBody[ConnTestODto])
  private val testKnownRts: HttpRoutes[IO]                                                                        =
    Http4sServerInterpreter[IO]().toRoutes(testKnownEpt.serverLogic { user => connId =>
      ConnSvc.testKnownConn(user, connId).toErrHandled
    })

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(testEpt, createEpt, listEpt, testKnownEpt).map(_.endpoint)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = testRts <+> createRts <+> listRts <+> testKnownRts

}
