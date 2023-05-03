package com.ilovedatajjia
package api.models

import api.helpers.DoobieUtils._
import cats.effect.IO
import java.sql.Timestamp

/**
 * DB representation of a cluster node.
 * @param id
 *   Node id
 * @param cpu
 *   Cpus usage ([[cpu.length]] == number of cores & in milli-cores)
 * @param ram
 *   Ram usage (in GiB)
 * @param ramTotal
 *   Ram total (in GiB)
 * @param registeredAt
 *   Registration to the database at
 * @param heartbeatAt
 *   Latest heartbeat to the database at
 */
case class NodeMod(id: Long,
                   cpu: List[Double],
                   ram: Double,
                   ramTotal: Double,
                   registeredAt: Timestamp,
                   heartbeatAt: Timestamp)

/**
 * [[NodeMod]] additions.
 */
object NodeMod {
  trait DB extends GenericDB[NodeMod] {

    /**
     * Constructor of [[NodeMod]].
     * @param cpu
     *   Cpus usage (between 0 and 1 for each core)
     * @param ram
     *   Ram usage (in GiB)
     * @param ramTotal
     *   Total ram (in GiB)
     * @param nowTimestamp
     *   Current timestamp
     * @return
     *   A new registered node model
     */
    def apply(cpu: List[Double], ram: Double, ramTotal: Double, nowTimestamp: Timestamp): IO[NodeMod] = insert(
      NodeMod(-1, cpu, ram, ramTotal, nowTimestamp, nowTimestamp))

  }
  object DB { implicit val impl: DB = new DB {} } // Auto-DI on import
}
