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
case class ConnMongoDbMod(id: Long, connId: Long, dbAuth: String, replicaSet: String, user: String, pwd: String) {

  /**
   * Get host port.
   */
  def hostPort: IO[List[ConnMongoDbMod.ConnMongoDbHostPortMod]] =
    ConnMongoDbMod.ConnMongoDbHostPortMod.select(fr"connMongoDbId = $id")

}

/**
 * Additional [[ConnMongoDbMod]] functions.
 */
object ConnMongoDbMod extends GenericMod[ConnMongoDbMod] {

  /**
   * Constructor of [[ConnMongoDbMod]].
   * @param connId
   *   [[ConnMod]] id
   * @param form
   *   [[ConnFormDtoIn.MongoDbFormDtoIn]] form
   * @return
   *   A new created mongodb connection
   */
  def apply(connId: Long, form: ConnFormDtoIn.MongoDbFormDtoIn): IO[ConnMongoDbMod] = for {
    connMongoDb <- insert(ConnMongoDbMod(-1, connId, form.dbAuth, form.replicaSet, form.user, form.pwd))
    _           <- form.hostPort.traverse(x => ConnMongoDbHostPortMod(connMongoDb.id, x.host, x.port))
  } yield connMongoDb

  /**
   * DB representation host & port for mongodb.
   */
  case class ConnMongoDbHostPortMod(id: Long, connMongoDbId: Long, host: String, port: Int)
  private object ConnMongoDbHostPortMod extends GenericMod[ConnMongoDbHostPortMod] {
    def apply(connMongoDbId: Long, host: String, port: Int): IO[ConnMongoDbHostPortMod] = insert(
      new ConnMongoDbHostPortMod(-1, connMongoDbId, host, port))
  }

}
