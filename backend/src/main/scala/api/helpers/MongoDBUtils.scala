package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import com.mongodb._
import com.mongodb.client._

/**
 * MongoDB related utils.
 */
object MongoDBUtils {

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
        s"mongodb://$user:$pwd@${hostPort.map { case (host, port) => s"$host:$port" }.mkString(",")}/?ssl=true&replicaSet=$replicaSet&authSource=$dbAuth&retryWrites=true&w=majority"
      MongoClients.create(MongoClientSettings.builder().applyConnectionString(new ConnectionString(connURI)).build())
    }
    .bracket(f)(conn => IO.interruptible(conn.close()))

}
