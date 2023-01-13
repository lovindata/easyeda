package com.ilovedatajjia
package config

import cats.effect.IO
import cats.implicits._
import com.comcast.ip4s.IpLiteralSyntax
import config.ConfigLoader._
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

/**
 * Application server.
 */
object AppServer {

  // FrontEnd routes
  private val indexHTMLRts: HttpRoutes[IO] = // "index.html" on "/*"
    Http4sServerInterpreter[IO]().toRoutes(fileGetServerEndpoint[IO](emptyInput)(s"$frontEndResourcePath/index.html"))
  private val staticFilesRts: HttpRoutes[
    IO
  ] = // Static files on "/view" (⚠️ It supposes frontend will never user "/view" as client-side route)
    Http4sServerInterpreter[IO]().toRoutes(filesServerEndpoints[IO]("view")(frontEndResourcePath))
  private val frontEndRts: HttpRoutes[IO]  = staticFilesRts <+> indexHTMLRts

  // BackEnd routes
  private val docsEndpoint: List[ServerEndpoint[Any, IO]] =
    SwaggerInterpreter().fromEndpoints[IO](List(???), "EloData_AppServer", "1.0") // On "/docs"
  private val docsRts: HttpRoutes[IO]    = Http4sServerInterpreter[IO]().toRoutes(docsEndpoint)
  private val backEndRts: HttpRoutes[IO] = docsRts <+> ???

  /**
   * Start the HTTP servers.
   */
  def run: IO[Unit] = for {
    // FrontEnd server
    _ <- EmberServerBuilder
           .default[IO]
           .withHost(ipv4"127.0.0.1") // Localhost equivalent
           .withPort(port"$frontEndPort")
           .withHttpApp(frontEndRts.orNotFound)
           .build
           .use(_ => IO.never)
           .start

    // BackEnd server
    _ <- EmberServerBuilder
           .default[IO]
           .withHost(ipv4"127.0.0.1") // Localhost equivalent
           .withPort(port"$backEndPort")
           .withHttpApp(backEndRts.orNotFound)
           .build
           .use(_ => IO.never)
  } yield ()

}
