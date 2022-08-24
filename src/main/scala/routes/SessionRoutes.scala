package com.ilovedatajjia
package routes

import cats.effect.IO
import cats.implicits._
import controllers.SessionController
import models.Session
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder // EntityEncoder for default Scala types
import org.http4s.dsl.io._
import routes.utils.Auth._
import routes.utils.Response._

/**
 * Routes related to sessions management.
 */
object SessionRoutes {

  // Define session creation route
  private val sessionCreationRoute: HttpRoutes[IO] = HttpRoutes.of[IO] { case POST -> Root / "create" =>
    SessionController.createSession.toResponse
  }

  // Define routes
  private val otherRoutes: AuthedRoutes[Session, IO] = AuthedRoutes.of {
    case GET -> Root / "status" as session     => Ok(session)
    case POST -> Root / "terminate" as session => SessionController.terminateSession(session).toResponse
    case GET -> Root / "listing" as session    => SessionController.listSessions(session).toResponse
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = sessionCreationRoute <+> withAuth(otherRoutes) // Always the non-auth routes first

}
