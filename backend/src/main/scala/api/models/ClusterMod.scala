package com.ilovedatajjia
package api.models

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
case class ClusterMod(id: Long,
                      cpu: List[Double],
                      ram: Double,
                      ramTotal: Double,
                      registeredAt: Timestamp,
                      heartbeatAt: Timestamp)

/**
 * Additional [[ClusterMod]] functions.
 */
object ClusterMod extends GenericMod[ClusterMod] {

  /**
   * Constructor of [[ClusterMod]].
   * @param cpu
   *   Cpus usage (in milli-cores)
   * @param ram
   *   Ram usage (in GiB)
   * @param ramTotal
   *   Total ram (in GiB)
   * @param nowTimestamp
   *   Current timestamp
   * @return
   *   A new registered node model
   */
  def apply(cpu: Array[Double], ram: Double, ramTotal: Double, nowTimestamp: Timestamp): IO[ClusterMod] = for {
    _ <- IO(???)
  } yield ???

}
