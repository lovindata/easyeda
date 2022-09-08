package com.ilovedatajjia
package routes.job

import cats.effect.IO
import cats.implicits.catsSyntaxTuple2Semigroupal
import fs2.Stream
import fs2.text
import io.circe.Json
import io.circe.fs2._
import io.circe.generic.auto._

import java.sql.Timestamp
import models.Session

import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.multipart.Multipart
import org.http4s.multipart.Part
import routes.utils.Auth._
import routes.utils.Request._
import routes.utils.Response._

import com.ilovedatajjia.controllers.JobController

/**
 * Routes related to CSV files management.
 */
object JobRoutes {

  // Define preview route
  private val previewRoute: AuthedRoutes[Session, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "preview" as session =>
      // Request with file upload and its parameters
      req.req.withJSONAndFileBytesMultipart("jsonParams", "fileBytes") {
        (jsonDrained: Json, fileDrained: String) => {
          JobController.computePreview(session, jsonDrained, fileDrained).toResponse
        }
      }
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
