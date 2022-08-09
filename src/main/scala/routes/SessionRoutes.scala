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

  // Define routes
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case POST -> Root / "create"   => SessionController.createSession.flatMap(Ok(_))
    case GET -> Root / "state"     => Ok(s"Hello state")
    case DELETE -> Root / "delete" => Ok(s"Hello deleted")
    case GET -> Root / "counts"    => Ok(s"Hello counted")
  }

}
