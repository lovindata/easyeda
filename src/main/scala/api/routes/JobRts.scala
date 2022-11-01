package com.ilovedatajjia
package api.routes

import api.controllers.JobCtrl
import api.controllers.SessionCtrl.withAuth
import api.helpers.AppLayerException.RouteLayerException
import api.models.SessionMod
import api.routes.utils.Request._
import api.routes.utils.Response._
import cats.effect.IO
import cats.implicits._
import fs2.Stream
import io.circe.Json
import org.http4s._
import org.http4s.dsl.io._

/**
 * Routes related to jobs management.
 */
object JobRts {

  // Query parameter(s)
  private object NbRows    extends ValidatingQueryParamDecoderMatcher[Int]("nbRows")
  private object MinColIdx extends ValidatingQueryParamDecoderMatcher[Int]("minColIdx")
  private object MaxColIdx extends ValidatingQueryParamDecoderMatcher[Int]("maxColIdx")

  // Define preview route
  private val previewRoute: AuthedRoutes[SessionMod, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "preview" :? NbRows(nbRows) :? MinColIdx(minColIdx) :? MaxColIdx(maxColIdx) as session =>
      (nbRows, minColIdx, maxColIdx)
        .mapN((_, _, _))
        .fold(
          // If failing query parameters
          parseFailures => RouteLayerException(parseFailures.toList.mkString("\n")).toResponseIO,

          // Else request with file upload and its parameters
          { case (nbRowsParsed, minColIdxParsed, maxColIdxParsed) =>
            req.req.withJSONAndFileBytesMultipart("fileImportOpt", "fileImport") {
              (fileImportOpt: Json, fileImport: Stream[IO, Byte], fileName: String) =>
                JobCtrl
                  .computePreview(session,
                                  fileImportOpt,
                                  fileImport,
                                  fileName,
                                  nbRowsParsed,
                                  minColIdxParsed,
                                  maxColIdxParsed)
                  .toResponse(Status.Ok)
            }
          }
        )
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
