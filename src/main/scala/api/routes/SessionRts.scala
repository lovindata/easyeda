package com.ilovedatajjia
package api.routes

import api.controllers.SessionCtrl
import api.controllers.SessionCtrl.withAuth
import api.dto.output.SessionStatusDtoOut
import api.models.SessionMod
import api.routes.utils.Response._
import api.services.SessionSvc
import cats.effect.IO
import cats.implicits._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

/**
 * Routes related to sessions management.
 */
object SessionRts {

  // Query parameter(s)
  object StateQueryParamMatcher extends QueryParamDecoderMatcher[String]("state")

  // Define session creation route
  private val sessionCreationRoute: HttpRoutes[IO] = HttpRoutes.of[IO] { case POST -> Root / "create" =>
    SessionSvc.createSession.toResponse(Status.Ok)
  }

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
      SessionCtrl.listSessions(state).toResponse(Status.Ok)
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = sessionCreationRoute <+> withAuth(otherRoutes) // Always the non-auth routes first

}
