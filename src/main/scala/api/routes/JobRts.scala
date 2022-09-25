package com.ilovedatajjia
package api.routes

import api.controllers.JobCtrl
import api.models.SessionMod
import api.routes.utils.Auth._
import api.routes.utils.Request._
import api.routes.utils.Response._
import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

/**
 * Routes related to jobs management.
 */
object JobRts {

  // Define preview route
  private val previewRoute: AuthedRoutes[SessionMod, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "preview" as session =>
      // Request with file upload and its parameters
      req.req.withJSONAndFileBytesMultipart("sparkArgs", "fileBytes") { (sparkArgs: Json, fileStr: String) =>
        JobCtrl.computePreview(session, sparkArgs, fileStr).toResponse
      }
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
