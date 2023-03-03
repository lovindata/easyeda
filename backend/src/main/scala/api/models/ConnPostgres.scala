package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormDtoIn
import cats.effect._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres.circe.json.implicits._
import doobie.postgres.implicits._ // Needed import for Meta mapping

/**
 * DB representation of a postgres connection.
 * @param id
 *   Postgres id
 * @param kind
 *   Connection kind
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
case class ConnPostgres(id: Long,
                        private val kind: String = "postgres",
                        host: String,
                        port: Int,
                        dbName: String,
                        user: String,
                        pwd: String)

/**
 * Additional [[ConnPostgres]] functions.
 */
object ConnPostgres extends GenericMod[ConnPostgres] {

  /**
   * Constructor of [[ConnPostgres]].
   * @param postgresForm
   *   [[ConnFormDtoIn.PostgresFormDtoIn]] form
   * @return
   *   A new created postgres connection
   */
  def apply(postgresForm: ConnFormDtoIn.PostgresFormDtoIn): IO[ConnPostgres] = insert(
    ConnPostgres(id = -1,
                 host = postgresForm.host,
                 port = postgresForm.port,
                 dbName = postgresForm.dbName,
                 user = postgresForm.user,
                 pwd = postgresForm.pwd))

}
