package com.ilovedatajjia
package api.models.conn

import api.dto.input.ConnFormIDto
import api.helpers.JdbcUtils
import api.models.ConnMod
import api.models.GenericDB
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
case class ConnPostgresMod(id: Long, connId: Long, host: String, port: Int, dbName: String, user: String, pwd: String) {

  /**
   * Test if connection is up.
   * @return
   *   If is up
   */
  def testIO: IO[Boolean] = JdbcUtils.testIO("org.postgresql.Driver",
                                             s"jdbc:postgresql://$host:$port/$dbName",
                                             "user"     -> user,
                                             "password" -> pwd)

  def listDB: IO[List[String]] = ???

}

/**
 * [[ConnPostgresMod]] additions.
 */
object ConnPostgresMod {
  trait DB extends GenericDB[ConnPostgresMod] {

    /**
     * Constructor of [[ConnPostgresMod]].
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
  object DB { implicit val impl: DB = new DB {} } // Auto-DI on import
}
