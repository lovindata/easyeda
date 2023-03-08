package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormDtoIn
import api.helpers.DoobieUtils._
import cats.effect.IO
import io.circe._
import io.circe.syntax._

/**
 * DB representation of a mongodb connection.
 * @param id
 *   Postgres id
 * @param connId
 *   Connection id
 * @param hostPort
 *   Host port
 * @param dbAuth
 *   Database authentication source
 * @param replicaSet
 *   Replica set configurations name
 * @param user
 *   User
 * @param pwd
 *   Password
 */
case class ConnMongoDbMod(id: Long,
                          connId: Long,
                          hostPort: List[Json],
                          dbAuth: String,
                          replicaSet: String,
                          user: String,
                          pwd: String)

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
  def apply(connId: Long, form: ConnFormDtoIn.MongoDbFormDtoIn): IO[ConnMongoDbMod] = insert(
    ConnMongoDbMod(-1, connId, form.hostPort.map(_.asJson), form.dbAuth, form.replicaSet, form.user, form.pwd))

}
