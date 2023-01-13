package com.ilovedatajjia
package api.routes

import api.controllers.UserCtrl._
import api.dto.input.CreateUserFormDtoIn
import api.dto.output.SessionStatusDtoOut
import api.dto.output.UserStatusDtoOut
import api.helpers.AppException
import api.helpers.AppException._
import api.models.SessionMod
import api.routes.utils.Response._
import api.services.SessionSvc
import cats.effect.IO
import cats.implicits._
import org.http4s._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for users management.
 */
object UserRts {

  // Create user account
  private val createEndpoint: PublicEndpoint[CreateUserFormDtoIn, AppException, UserStatusDtoOut, Any] = endpoint
    .summary("Create user account")
    .post
    .in("user" / "create")
    .in(jsonBody[CreateUserFormDtoIn])
    .out(jsonBody[UserStatusDtoOut])
    .errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[AppException]))
  private val createRts: HttpRoutes[IO]                                                                =
    Http4sServerInterpreter[IO]().toRoutes(createEndpoint.serverLogic(createUserAccount(_).toErrHandled))

  // Define retrieve session status, terminate session & list all active sessions routes
  private val otherRoutes: AuthedRoutes[SessionMod, IO] = AuthedRoutes.of {
    case GET -> Root / "status" as session                                   =>
      Ok(
        SessionStatusDtoOut(session.id,
                            session.createdAt.toString,
                            session.updatedAt.toString,
                            session.terminatedAt.map(_.toString)))
    case POST -> Root / "terminate" as session                               =>
      SessionSvc.terminateSession(session).toResponse(Status.Ok)
    case GET -> Root / "listing" :? StateQueryParamMatcher(state) as session =>
      UserCtrl.listSessions(state).toResponse(Status.Ok)
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = createRts <+> withAuth(otherRoutes) // Always the non-auth routes first

}
