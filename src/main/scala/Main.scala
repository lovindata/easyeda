package com.ilovedatajjia

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import server.Http4sServer

/**
 * Application entrypoint.
 */
object Main extends IOApp {

  // Start server
  override def run(args: List[String]): IO[ExitCode] = Http4sServer.runServer()

}
