package com.ilovedatajjia

import cats.effect.IO
import cats.effect.IOApp
import services.AppServer
import services.SparkServer

/**
 * Application entrypoint.
 */
object Main extends IOApp.Simple {

  /**
   * Run all the required services and server.
   */
  override def run: IO[Unit] = SparkServer.run >> AppServer.run

}
