package com.ilovedatajjia
package api.models.conn

import api.dto.input.ConnFormIDto
import api.helpers.DoobieUtils._
import api.helpers.MongoUtils
import api.models._
import cats.effect.IO
import cats.implicits._

/**
 * DB representation of a mongodb connection.
 * @param id
 *   Postgres id
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
case class ConnMongoMod(id: Long, connId: Long, dbAuth: String, replicaSet: String, user: String, pwd: String) {

  /**
   * Test if connection is up.
   * @return
   *   If is up
   */
  def testIO: IO[Boolean] = for {
    hostPort <- ConnMongoMod.ConnMongoHostPortMod.select(fr"conn_mongo_id = $id").map(_.map(x => (x.host, x.port)))
    isUp     <- MongoUtils.testIO(hostPort, dbAuth, replicaSet, user, pwd)
  } yield isUp

}

/**
 * [[ConnMongoMod]] additions.
 */
object ConnMongoMod extends GenericDB[ConnMongoMod] {

  /**
   * Constructor of [[ConnMongoMod]].
   * @param connId
   *   [[ConnMod]] id
   * @param form
   *   [[ConnFormIDto.MongoFormIDto]] form
   * @return
   *   A new created mongodb connection
   */
  def apply(connId: Long, form: ConnFormIDto.MongoFormIDto): IO[ConnMongoMod] = for {
    connMongo <- insert(ConnMongoMod(-1, connId, form.dbAuth, form.replicaSet, form.user, form.pwd))
    _         <- form.hostPort.traverse(x => ConnMongoHostPortMod(connMongo.id, x.host, x.port))
  } yield connMongo

  /**
   * DB representation host & port for mongodb.
   */
  private case class ConnMongoHostPortMod(id: Long, connMongoId: Long, host: String, port: Int)
  private object ConnMongoHostPortMod extends GenericDB[ConnMongoHostPortMod] {
    def apply(connMongoId: Long, host: String, port: Int): IO[ConnMongoHostPortMod] = insert(
      ConnMongoHostPortMod(-1, connMongoId, host, port))
  }

}
