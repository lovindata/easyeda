package com.ilovedatajjia
package routes.session

import cats.effect.IO
import cats.implicits._
import java.util.UUID
import models.Session
import org.http4s.HttpRoutes
import routes.utils.CustomEncoders._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

/**
 * Swagger documentation.
 */
object SessionSwagger {

  // All endpoints
  private val createEndpoint: PublicEndpoint[Unit, Unit, String, Any]          =
    endpoint.post.in("create").out(stringBody)
  private val statusEndpoint: PublicEndpoint[Unit, Unit, Session, Any]         =
    endpoint.post.in("status").out(jsonBody[Session])
  private val terminateEndpoint: PublicEndpoint[Unit, Unit, Session, Any]      =
    endpoint.post.in("terminate").out(jsonBody[Session])
  private val listingEndpoint: PublicEndpoint[Unit, Unit, Array[Session], Any] =
    endpoint.post.in("listing").out(jsonBody[Array[Session]])

  // Response examples
  private val createResponse: ServerEndpoint[Any, IO]    =
    createEndpoint.serverLogicSuccess((_: Unit) => IO("00000000-0000-0000-0000-000000000000"))
  private val statusResponse: ServerEndpoint[Any, IO]    =
    statusEndpoint.serverLogicSuccess((_: Unit) =>
      IO(Session(UUID.fromString("00000000-0000-0000-0000-000000000000"), "<someValue>", null, null, None)))
  private val terminateResponse: ServerEndpoint[Any, IO] =
    terminateEndpoint.serverLogicSuccess((_: Unit) =>
      Session(UUID.fromString("00000000-0000-0000-0000-000000000001"), "<someValue>"))
  private val listingResponse: ServerEndpoint[Any, IO]   =
    listingEndpoint.serverLogicSuccess((_: Unit) =>
      Array(Session(UUID.fromString("<someValue>"), "<someValue>"),
            Session(UUID.fromString("<someValue>"), "<someValue>")).toList.sequence.map(_.toArray))

  // Merge all routes
  private val apiEndpoints: List[ServerEndpoint[Any, IO]] =
    List(createResponse, statusResponse, terminateResponse, listingResponse)
  private val docEndpoints: List[ServerEndpoint[Any, IO]] = SwaggerInterpreter()
    .fromServerEndpoints[IO](apiEndpoints, "learning-http4s", "0.1.0-SNAPSHOT")
  private val all: List[ServerEndpoint[Any, IO]]          = apiEndpoints ++ docEndpoints
  val routes: HttpRoutes[IO]                              = Http4sServerInterpreter[IO]().toRoutes(all)

}
