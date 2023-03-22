package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormIDto
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
   *
   * @param connId
   *   [[ConnMod]] id
   * @param form
   *   [[ConnFormIDto.PostgresFormIDto]] form
   * @return
   *   A new created postgres connection
   */
  def apply(connId: Long, form: ConnFormIDto.PostgresFormIDto): IO[ConnPostgresMod] = insert(
    ConnPostgresMod(-1, connId, form.host, form.port, form.dbName, form.user, form.pwd))

}
