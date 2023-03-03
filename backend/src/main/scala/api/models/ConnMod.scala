package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormDtoIn
import cats.effect._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres.circe.json.implicits._
import doobie.postgres.implicits._ // Needed import for Meta mapping

/**
 * DB representation of connections.
 * @param id
 *   Token id
 * @param userId
 *   User id
 * @param kind
 *   Connection kind
 * @param connId
 *   Connection id
 * @param name
 *   Connection name
 */
case class ConnMod(id: Long, userId: Long, kind: String, connId: Long, name: String)

/**
 * Additional [[ConnMod]] functions.
 */
object ConnMod extends GenericMod[ConnMod] {

  /**
   * Constructor of [[ConnMod]].
   * @param userId
   *   User id
   * @param connForm
   *   Connection form
   * @return
   *   A new created connection
   */
  def apply(userId: Long, connForm: ConnFormDtoIn): IO[ConnMod] = connForm match {
    case form: ConnFormDtoIn.PostgresFormDtoIn =>
      ConnPostgres(form).flatMap { case ConnPostgres(connId, _, kind, _, _, _, _) =>
        insert(ConnMod(-1, userId, kind, connId, form.name))
      }
    case form: ConnFormDtoIn.MongoDbFormDtoIn  => ???
  }

}
