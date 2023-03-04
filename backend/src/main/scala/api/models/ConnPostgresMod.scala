package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormDtoIn
import cats.effect.IO

/**
 * DB representation of a postgres connection.
 * @param id
 *   Postgres id
 * @param connId
 *   Connection id
 * @param host
 *   Host
 * @param port
 *   Port
 * @param dbName
 *   Database name
 * @param user
 *   User
 * @param pwd
 *   Password
 */
case class ConnPostgresMod(id: Long, connId: Long, host: String, port: Int, dbName: String, user: String, pwd: String)

/**
 * Additional [[ConnPostgresMod]] functions.
 */
object ConnPostgresMod extends GenericMod[ConnPostgresMod] {

  /**
   * Constructor of [[ConnPostgresMod]].
   * @param connId
   *   [[ConnMod]] id
   * @param postgresForm
   *   [[ConnFormDtoIn.PostgresFormDtoIn]] form
   * @return
   *   A new created postgres connection
   */
  def apply(connId: Long, postgresForm: ConnFormDtoIn.PostgresFormDtoIn): IO[ConnPostgresMod] = insert(
    ConnPostgresMod(-1,
                    connId,
                    postgresForm.host,
                    postgresForm.port,
                    postgresForm.dbName,
                    postgresForm.user,
                    postgresForm.pwd))

}
