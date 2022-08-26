package com.ilovedatajjia

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import servers.AppServer
import servers.SwaggerServer

/**
 * Application entrypoint.
 */
object Main extends IOApp {

  /**
   * Entrypoint of the application.
   * @param args
   *   Arguments
   * @return
   *   Exit code
   */
  override def run(args: List[String]): IO[ExitCode] = {
    SwaggerServer.runServer()
    AppServer.runServer()
  }

}
