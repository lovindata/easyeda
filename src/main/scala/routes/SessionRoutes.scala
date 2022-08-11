package com.ilovedatajjia
package routes

import cats.effect._
import controllers.SessionController
import org.http4s._
import org.http4s.dsl.io._
import routes.utils._

/**
 * Routes related to sessions management.
 */
object SessionRoutes {

  // Define session creation route
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case POST -> Root / "create" =>
    SessionController.createSession.redeemWith(
      (e: Throwable) => InternalServerError(e.toString),
      (authToken: String) => Ok(authToken)
    )
  }

  // Define routes
  val otherRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "status"    => Ok(s"Hello state")
    case DELETE -> Root / "delete" => Ok(s"Hello deleted")
    case GET -> Root / "counts"    => Ok(s"Hello counted")
  }

}
