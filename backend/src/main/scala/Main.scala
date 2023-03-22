package com.ilovedatajjia

import cats.effect.IO
import cats.effect.IOApp
import config.BackendServer

/**
 * Application configurations & entrypoint.
 */
object Main extends IOApp.Simple {

  /**
   * Run all the required requisites.
   */
  override def run: IO[Unit] = BackendServer.run

}
