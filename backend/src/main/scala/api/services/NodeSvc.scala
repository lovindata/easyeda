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

/**
 * Service layer for cluster monitoring.
 */
trait NodeSvc {

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
  def toDto(implicit nodeModDB: NodeMod.DB): IO[NodeStatusODto] = for {
    // Retrieve
    timestampAlive <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis - aliveInterval.toMillis))
    nodesMod       <- nodeModDB.select(fr"heartbeat_at >= $timestampAlive")

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
  def report(implicit nodeModDB: NodeMod.DB): IO[Unit] = (for {
    // Get usages
    hardware <- IO(new SystemInfo().getHardware)
    cpu      <- IO.blocking(hardware.getProcessor.getProcessorCpuLoad(heartbeatInterval.toMillis).toList)
    ramTotal <- IO(hardware.getMemory.getTotal.toDouble / 1073741824L.toDouble)
    ram      <- IO(ramTotal - (hardware.getMemory.getAvailable.toDouble / 1073741824L.toDouble))

    // Report usages
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    _            <- nodeId match {
                      case None         =>
                        nodeModDB(cpu, ram, ramTotal, nowTimestamp).map(node => nodeId = node.id.some)
                      case Some(nodeId) =>
                        nodeModDB
                          .select(nodeId)
                          .map(_.copy(cpu = cpu, ram = ram, ramTotal = ramTotal, heartbeatAt = nowTimestamp))
                          .flatMap(nodeModDB.update)
                    }

    // Rec
    _            <- report
  } yield ()).start.void

}

/**
 * Auto-DI on import.
 */
object NodeSvc { implicit val impl: NodeSvc = new NodeSvc {} }
