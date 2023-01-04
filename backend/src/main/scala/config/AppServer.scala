package com.ilovedatajjia
package config

import ConfigLoader.appPort
import api.routes.JobRts
import api.routes.SessionRts
import cats.effect.IO
import com.comcast.ip4s._
import org.http4s._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.staticcontent.FileService
import org.http4s.server.staticcontent.fileService
import org.http4s.server.staticcontent.resourceServiceBuilder

/**
 * Application server.
 */
object AppServer {

  // Build SwaggerUI route (it allows rendering of all files in `swagger` resource folder at server URL path `/`)
  val swaggerIURts: HttpRoutes[IO] = resourceServiceBuilder[IO]("swagger").toRoutes
  val reactAppRts: HttpRoutes[IO]  =
    fileService[IO](FileService.Config("D:\\prog\\proj\\easyeda\\frontend\\build")) // TODO

  // Retrieve all api route(s)
  val apiRts: HttpRoutes[IO] = Router("/session" -> SessionRts.routes, "/job" -> JobRts.routes)

  // Combine all route(s)
  val combinedRts: HttpApp[IO] = Router("/swagger" -> swaggerIURts, "/api" -> apiRts, "/" -> reactAppRts).orNotFound

  /**
   * Build & Run the HTTP4s server.
   */
  def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"127.0.0.1") // localhost equivalent
    .withPort(appPort)
    .withHttpApp(combinedRts)
    .build
    .use(_ => IO.never)
    .void

}
