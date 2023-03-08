package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import com.mongodb._
import com.mongodb.client._
import scala.concurrent.duration._

/**
 * MongoDB related utils.
 */
object MongoDbUtils {

  /**
   * Auto-closable provided connection to run an execution.
   * @param hostPort
   *   Couple host port
   * @param dbAuth
   *   Authentication source database
   * @param replicaSet
   *   Replica set name configuration
   * @param user
   *   User
   * @param pwd
   *   Password
   * @param f
   *   Runnable
   * @tparam A
   *   Output datatype
   * @return
   *   Output from runnable
   */
  def connIO[A](hostPort: List[(String, Int)], dbAuth: String, replicaSet: String, user: String, pwd: String)(
      f: MongoClient => IO[A]): IO[A] = IO
    .interruptible {
      val connURI =
        s"mongodb://$user:$pwd@${hostPort.map { case (host, port) => s"$host:$port" }.mkString(",")}/" +
          s"?ssl=true&replicaSet=$replicaSet&authSource=$dbAuth&retryWrites=true&w=majority"
      MongoClients.create(
        MongoClientSettings
          .builder()
          .applyConnectionString(new ConnectionString(connURI))
          .applyToClusterSettings(x => x.serverSelectionTimeout(1, SECONDS))
          .build())
    }
    .bracket(f)(conn => IO.interruptible(conn.close()))

}
