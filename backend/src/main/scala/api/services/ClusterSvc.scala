package com.ilovedatajjia
package api.services

import cats.effect.IO
import scala.concurrent.duration._

/**
 * Service layer for cluster monitoring.
 */
object ClusterSvc {

  /**
   * Report current CPU & RAM usage.
   * @param interval
   *   Interval between reports
   */
  def reportVital(interval: FiniteDuration = 5.seconds): IO[Unit] = ???

}
