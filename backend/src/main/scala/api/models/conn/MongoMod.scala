package com.ilovedatajjia
package api.models.conn

import api.dto.input.ConnFormIDto
import api.dto.output.ConnTestODto
import api.helpers.BackendException.AppException
import api.helpers.DoobieUtils._
import api.helpers.MongoUtils
import api.models._
import cats.effect.IO
import cats.implicits._

/**
 * DB representation of a mongodb connection.
 * @param id
 *   Mongo id
 * @param connId
 *   Connection id
 * @param dbAuth
 *   Database authentication source
 * @param replicaSet
 *   Replica set configurations name
 * @param user
 *   User
 * @param pwd
 *   Password
 */
case class MongoMod(id: Long, connId: Long, dbAuth: String, replicaSet: String, user: String, pwd: String) {

  /**
   * Get couple(s) host port corresponding to the connection.
   * @return
   *   List of couple(s) host port
   */
  private def hostPort: IO[List[(String, Int)]] =
    MongoMod.MongoHostPortMod.select(fr"mongo_id = $id").map(_.map(x => (x.host, x.port)))

  /**
   * Test if connection is up.
   * @return
   *   [[ConnTestODto]]
   */
  def testConn: IO[ConnTestODto] = for {
    hostPorts <- hostPort
    dto       <- MongoUtils.testConn(hostPorts, dbAuth, replicaSet, user, pwd).map { case (isUp, errMsg) =>
                   ConnTestODto(isUp, errMsg)
                 }
  } yield dto

  /**
   * List databases.
   * @return
   *   List of databases name
   */
  def listDb: IO[List[String]] = for {
    hostPort <- hostPort
    dbs      <- MongoUtils.connIO(hostPort, dbAuth, replicaSet, user, pwd) { conn =>
                  IO.fromFuture(IO.interruptible(conn.listDatabaseNames().collect().head()))
                }
  } yield dbs.toList

  /**
   * List collections for a given database.
   * @param db
   *   Database
   * @return
   *   List of collections name
   */
  def listColl(db: String): IO[List[String]] = for {
    // Verify database existence (because query does not manage it)
    dbs <- listDb
    _   <- IO.raiseUnless(dbs.contains(db))(AppException(s"ERROR: database \"$db\" does not exist"))

    // Do query
    hostPort    <- hostPort
    collections <- MongoUtils.connIO(hostPort, dbAuth, replicaSet, user, pwd) { conn =>
                     IO.fromFuture(IO.interruptible(conn.getDatabase(db).listCollectionNames().collect().head()))
                   }
  } yield collections.toList

}

/**
 * [[MongoMod]] additions.
 */
object MongoMod {

  /**
   * DB layer.
   */
  trait DB extends GenericDB[MongoMod] {

    /**
     * Constructor of [[MongoMod]].
     *
     * @param connId
     *   [[ConnMod]] id
     * @param form
     *   [[ConnFormIDto.MongoFormIDto]] form
     * @return
     *   A new created mongodb connection
     */
    def apply(connId: Long, form: ConnFormIDto.MongoFormIDto): IO[MongoMod] = for {
      connMongo <- insert(MongoMod(-1, connId, form.dbAuth, form.replicaSet, form.user, form.pwd))
      _         <- form.hostPort.traverse(x => MongoHostPortMod(connMongo.id, x.host, x.port))
    } yield connMongo

  }
  object DB { implicit val impl: DB = new DB {} } // Auto-DI on import

  /**
   * DB representation host & port for mongodb.
   */
  private case class MongoHostPortMod(id: Long, mongoId: Long, host: String, port: Int)
  private object MongoHostPortMod extends GenericDB[MongoHostPortMod] {
    def apply(mongoId: Long, host: String, port: Int): IO[MongoHostPortMod] = insert(
      MongoHostPortMod(-1, mongoId, host, port))
  }

}
