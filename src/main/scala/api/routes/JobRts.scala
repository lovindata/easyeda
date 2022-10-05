package com.ilovedatajjia
package api.routes

import api.controllers.JobCtrl
import api.models.SessionMod
import api.routes.utils.Auth._
import api.routes.utils.Request._
import api.routes.utils.Response._
import cats.effect.IO
import cats.implicits._
import fs2.Stream
import io.circe.Json
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
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
          parseFailures => BadRequest(parseFailures.toList.mkString("\n")),
          { case (nbRowsParsed, minColIdxParsed, maxColIdxParsed) =>
            // Request with file upload and its parameters
            req.req.withJSONAndFileBytesMultipart("fileImportOpt", "fileImport") {
              (fileImportOpt: Json, fileImport: Stream[IO, Byte]) =>
                JobCtrl
                  .computePreview(session, fileImportOpt, fileImport, nbRowsParsed, minColIdxParsed, maxColIdxParsed)
                  .toResponse
            }
          }
        )
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
