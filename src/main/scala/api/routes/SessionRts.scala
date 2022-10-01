package com.ilovedatajjia
package api.routes

import api.controllers.SessionCtrl
import api.models.SessionMod
import api.routes.utils.Auth._
import api.routes.utils.Response._
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
    SessionCtrl.createSession.toResponse
  }

  // Define retrieve session status, terminate session & list all active sessions routes
  private val otherRoutes: AuthedRoutes[SessionMod, IO] = AuthedRoutes.of {
    case GET -> Root / "status" as session                                   => Ok(SessionCtrl.renderSession(session))
    case POST -> Root / "terminate" as session                               => SessionCtrl.terminateSession(session).toResponse
    case GET -> Root / "listing" :? StateQueryParamMatcher(state) as session =>
      SessionCtrl.listSessions(session, state).toResponse
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = sessionCreationRoute <+> withAuth(otherRoutes) // Always the non-auth routes first

}
