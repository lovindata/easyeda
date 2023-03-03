package com.ilovedatajjia
package config

import api.routes._
import cats.effect.IO
import cats.implicits._
import com.comcast.ip4s._
import config.ConfigLoader._
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.CORS
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

/**
 * Application server.
 */
object AppServer {

  // FrontEnd routes
  private val staticFilesRts: HttpRoutes[IO] = // Static files on "/assets"
    Http4sServerInterpreter[IO]().toRoutes(filesServerEndpoints[IO]("assets")(s"$frontEndResourcePath/assets"))
  private val indexHTMLRts: HttpRoutes[IO]   = // "index.html" on "/*"
    Http4sServerInterpreter[IO]().toRoutes(fileGetServerEndpoint[IO](emptyInput)(s"$frontEndResourcePath/index.html"))
  private val frontEndRts: HttpRoutes[IO]    = staticFilesRts <+> indexHTMLRts

  // BackEnd routes
  private val docsEpt: List[ServerEndpoint[Any, IO]] =
    SwaggerInterpreter().fromEndpoints[IO](UserRts.docEpt ++ ConnRts.docEpt, "AppServer", "1.0") // On "/docs"
  private val docsRts: HttpRoutes[IO]    = Http4sServerInterpreter[IO]().toRoutes(docsEpt)
  private val backEndRts: HttpRoutes[IO] = docsRts <+> UserRts.appRts <+> ConnRts.appRts

  /**
   * Start the HTTP servers.
   */
  def run: IO[Unit] = for {
    // FrontEnd server
    _ <- EmberServerBuilder
           .default[IO]
           .withHost(ipv4"127.0.0.1") // Localhost equivalent
           .withPort(Port.fromString(frontEndPort).get)
           .withHttpApp(frontEndRts.orNotFound)
           .build
           .use(_ => IO.never)
           .start

    // BackEnd server
    _ <- EmberServerBuilder
           .default[IO]
           .withHost(ipv4"127.0.0.1") // Localhost equivalent
           .withPort(Port.fromString(backEndPort).get)
           .withHttpApp(CORS.policy.withAllowOriginAll(backEndRts).orNotFound)
           .build
           .use(_ => IO.never)
  } yield ()

}
