package com.ilovedatajjia

import cats.effect.IO
import cats.effect.IOApp
import config.AppServer
import config.SparkServer

/**
 * Application entrypoint.
 */
object Main extends IOApp.Simple {

  /**
   * Run all the required services and server.
   */
  override def run: IO[Unit] = SparkServer.run >> AppServer.run

}
