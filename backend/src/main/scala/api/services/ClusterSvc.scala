package com.ilovedatajjia
package api.services

import cats.effect._
import cats.implicits._
import java.sql.Timestamp
import oshi.SystemInfo
import scala.concurrent.duration._

/**
 * Service layer for cluster monitoring.
 */
object ClusterSvc {

  /**
   * Node identifier.
   * @note
   *   Initialized after registration to the global database.
   */
  private var nodeId: Option[Long] = None

  /**
   * Report current CPU & RAM usage.
   * @param interval
   *   Interval between reports
   */
  def report(interval: FiniteDuration = 5.seconds): IO[Unit] = for {
    // Get usages
    hardware <- IO(new SystemInfo().getHardware)
    cpu      <- IO.blocking(hardware.getProcessor.getProcessorCpuLoad(interval.toMillis))
    ramTotal <- IO(hardware.getMemory.getTotal.toDouble / 1073741824L.toDouble)
    ram      <- IO(ramTotal - (hardware.getMemory.getAvailable.toDouble / 1073741824L.toDouble))

    // Report usages
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
  } yield ()

}
