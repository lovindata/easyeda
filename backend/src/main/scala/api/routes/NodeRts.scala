package com.ilovedatajjia
package api.routes

import api.dto.output._
import api.helpers.BackendException
import api.helpers.BackendException._
import api.services.NodeSvc
import cats.effect.IO
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for nodes management.
 */
object NodeRts extends GenericRts {

  // Retrieve nodes status
  private val getEpt: PublicEndpoint[Unit, BackendException, NodeStatusODto, Any] = ept
    .summary("retrieve node status")
    .get
    .in("node" / "status")
    .out(jsonBody[NodeStatusODto])
  private val getRts: HttpRoutes[IO]                                              =
    Http4sServerInterpreter[IO]().toRoutes(getEpt.serverLogic(_ => NodeSvc.impl.toDto.toErrHandled))

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(getEpt)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = getRts

}
