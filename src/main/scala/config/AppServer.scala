package com.ilovedatajjia
package config

import api.routes.JobRts
import api.routes.SessionRts
import cats.effect._
import com.comcast.ip4s._
import org.http4s._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router

/**
 * Application server.
 */
object AppServer {

  // Retrieve all route(s)
  val combinedRoutes: HttpApp[IO] = Router("/session" -> SessionRts.routes, "/job" -> JobRts.routes).orNotFound

  // Build the server
  val serverBuilder: EmberServerBuilder[IO] = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"127.0.0.1")         // localhost equivalent
    .withPort(port"8080")
    .withHttpApp(combinedRoutes)
    .withReceiveBufferSize(256 * 1024) // Default value is 256 * 1024

  /**
   * Run the HTTP4s server.
   */
  def run: IO[Unit] = serverBuilder.build.use(_ => IO.never).void

}
