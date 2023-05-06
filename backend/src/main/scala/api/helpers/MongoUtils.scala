package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import cats.implicits._
import org.bson._
import org.mongodb.scala._
import scala.concurrent.duration._

/**
 * MongoDB related utils.
 */
object MongoUtils {

  /**
   * Auto-closable provided connection to run an execution.
   * @param hostPort
   *   Couple(s) host port
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
      MongoClient(
        MongoClientSettings
          .builder()
          .applyConnectionString(ConnectionString(connURI))
          .applyToClusterSettings(x => x.serverSelectionTimeout(1, SECONDS))
          .build())
    }
    .bracket(f)(conn => IO.interruptible(conn.close))

  /**
   * Test MongoDB connection.
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
   * @return
   *   [[Boolean]] if connection available and optional error message
   */
  def testConn(hostPort: List[(String, Int)],
               dbAuth: String,
               replicaSet: String,
               user: String,
               pwd: String): IO[(Boolean, Option[String])] = connIO(hostPort, dbAuth, replicaSet, user, pwd)(conn =>
    IO.interruptible {
      conn
        .getDatabase(dbAuth)
        .runCommand(
          new BsonDocument("ping", new BsonInt64(1))
        ) // Verify connection https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/connect/
      // & https://www.mongodb.com/docs/manual/reference/command/ping/
      (true, none)
    }).recover(t => (false, t.getMessage.some))

}
