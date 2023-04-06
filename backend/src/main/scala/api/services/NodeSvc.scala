package com.ilovedatajjia
package api.services

import api.dto.output.NodeStatusODto
import api.helpers.DoobieUtils._
import api.models.NodeMod
import cats.effect._
import cats.implicits._
import config.ConfigLoader._
import java.sql.Timestamp
import oshi.SystemInfo
import scala.concurrent.duration._

/**
 * Service layer for cluster monitoring.
 */
object NodeSvc {

  /**
   * Node identifier.
   * @note
   *   Initialized after registration to the global database.
   */
  private var nodeId: Option[Long] = None

  /**
   * Retrieve node(s) status.
   * @return
   *   DTO version of node(s) status
   */
  def toDto: IO[NodeStatusODto] = for {
    // Retrieve
    timestampAlive <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis - aliveInterval.seconds.toMillis))
    nodesMod       <- NodeMod.select(fr"heartbeat_at >= $timestampAlive")

    // Prepare info
    nbNodes  <- IO(nodesMod.length)
    cpuTotal <- IO(nodesMod.map(_.cpu.length).sum)
    cpu      <- IO(nodesMod.map(_.cpu.sum).sum * cpuTotal)
    ramTotal <- IO(nodesMod.map(_.ramTotal).sum)
    ram      <- IO(nodesMod.map(_.ram).sum)
  } yield NodeStatusODto(nbNodes, cpu, cpuTotal, ram, ramTotal)

  /**
   * Report current CPU & RAM usage.
   */
  def report: IO[Unit] = (for {
    // Get usages
    hardware <- IO(new SystemInfo().getHardware)
    cpu      <- IO.blocking(hardware.getProcessor.getProcessorCpuLoad(heartbeatInterval.seconds.toMillis).toList)
    ramTotal <- IO(hardware.getMemory.getTotal.toDouble / 1073741824L.toDouble)
    ram      <- IO(ramTotal - (hardware.getMemory.getAvailable.toDouble / 1073741824L.toDouble))

    // Report usages
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    _            <- nodeId match {
                      case None         =>
                        NodeMod(cpu, ram, ramTotal, nowTimestamp).map(node => nodeId = node.id.some)
                      case Some(nodeId) =>
                        NodeMod
                          .select(nodeId)
                          .map(_.copy(cpu = cpu, ram = ram, ramTotal = ramTotal, heartbeatAt = nowTimestamp))
                          .flatMap(NodeMod.update)
                    }

    // Rec
    _            <- report
  } yield ()).start.void

}
