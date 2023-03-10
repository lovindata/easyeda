package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormDtoIn
import api.helpers.DoobieUtils._
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
   * Get host port.
   */
  def hostPort: IO[List[(String, Int)]] =
    ConnMongoMod.ConnMongoHostPortMod.select(fr"conn_mongo_id = $id").map(_.map(x => (x.host, x.port)))

}

/**
 * Additional [[ConnMongoMod]] functions.
 */
object ConnMongoMod extends GenericMod[ConnMongoMod] {

  /**
   * Constructor of [[ConnMongoMod]].
   *
   * @param connId
   *   [[ConnMod]] id
   * @param form
   *   [[ConnFormDtoIn.MongoFormDtoIn]] form
   * @return
   *   A new created mongodb connection
   */
  def apply(connId: Long, form: ConnFormDtoIn.MongoFormDtoIn): IO[ConnMongoMod] = for {
    connMongo <- insert(ConnMongoMod(-1, connId, form.dbAuth, form.replicaSet, form.user, form.pwd))
    _         <- form.hostPort.traverse(x => ConnMongoHostPortMod(connMongo.id, x.host, x.port))
  } yield connMongo

  /**
   * DB representation host & port for mongodb.
   */
  private case class ConnMongoHostPortMod(id: Long, connMongoId: Long, host: String, port: Int)
  private object ConnMongoHostPortMod extends GenericMod[ConnMongoHostPortMod] {
    def apply(connMongoId: Long, host: String, port: Int): IO[ConnMongoHostPortMod] = insert(
      new ConnMongoHostPortMod(-1, connMongoId, host, port))
  }

}
