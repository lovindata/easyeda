package com.ilovedatajjia
package api.routes

import api.controllers.JobCtrl
import api.models.SessionMod
import api.routes.utils.Auth._
import api.routes.utils.Request._
import api.routes.utils.Response._
import cats.effect.IO
import cats.implicits._
import io.circe.Json
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

/**
 * Routes related to jobs management.
 */
object JobRts {

  // Query parameter(s)
  private object NbRows extends OptionalValidatingQueryParamDecoderMatcher[Int]("nbRows")
  private object NbCols extends OptionalValidatingQueryParamDecoderMatcher[Int]("nbCols")

  // Define preview route
  private val previewRoute: AuthedRoutes[SessionMod, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "preview" :? NbRows(nbRows) :? NbCols(nbCols) as session =>
      (nbRows, nbCols).mapN((_, _)) match {
        case None                   => BadRequest("Please make sure to provide parameters `nbRows` & `nbCols` in the route path")
        case Some((nbRows, nbCols)) =>
          (nbRows, nbCols)
            .mapN((_, _))
            .fold(
              parseFailures => BadRequest(parseFailures.foldLeft("")(_ + "\n" + _)),
              { case (nbRowsParsed, nbColsParsed) =>
                // Request with file upload and its parameters
                req.req.withJSONAndFileBytesMultipart("sparkArgs", "fileBytes") { (sparkArgs: Json, fileStr: String) =>
                  JobCtrl.computePreview(session, sparkArgs, fileStr, nbRowsParsed, nbColsParsed).toResponse
                }
              }
            )
      }
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
