package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormDtoIn
import api.helpers.ConnKindEnum
import cats.effect._

/**
 * DB representation of connections.
 * @param id
 *   Token id
 * @param userId
 *   User id
 * @param kind
 *   Connection kind
 * @param name
 *   Connection name
 */
case class ConnMod(id: Long, userId: Long, kind: ConnKindEnum.ConnKind, name: String)

/**
 * Additional [[ConnMod]] functions.
 */
object ConnMod extends GenericMod[ConnMod] {

  /**
   * Constructor of [[ConnMod]].
   * @param userId
   *   User id
   * @param form
   *   Connection form
   * @return
   *   A new created connection
   */
  def apply(userId: Long, form: ConnFormDtoIn): IO[ConnMod] = form match {
    case form: ConnFormDtoIn.PostgresFormDtoIn =>
      for {
        conn <- insert(ConnMod(-1, userId, ConnKindEnum.postgres, form.name))
        _    <- ConnPostgresMod(conn.id, form)
      } yield conn
    case form: ConnFormDtoIn.MongoDbFormDtoIn  =>
      for {
        conn <- insert(ConnMod(-1, userId, ConnKindEnum.mongodb, form.name))
        _    <- ConnMongoDbMod(conn.id, form)
      } yield conn
  }

}
