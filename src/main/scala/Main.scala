package com.ilovedatajjia

import api.controllers.SessionCtrl
import cats.effect.IO
import cats.effect.IOApp
import config.AppServer
import config.SparkServer

/**
 * Application configurations & entrypoint.
 */
object Main extends IOApp.Simple {

  /**
   * Run all the required requisites.
   */
  override def run: IO[Unit] = SessionCtrl.initProcessExistingSessions >> SparkServer.run >> AppServer.run

}
