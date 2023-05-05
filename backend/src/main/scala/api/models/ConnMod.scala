package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormIDto
import api.helpers.ConnTypeEnum
import api.helpers.DoobieUtils._
import api.models.conn._
import cats.effect._

/**
 * DB representation of connections.
 * @param id
 *   Token id
 * @param userId
 *   User id
 * @param type
 *   Connection type
 * @param name
 *   Connection name
 */
case class ConnMod(id: Long, userId: Long, `type`: ConnTypeEnum.ConnType, name: String) {

  /**
   * Test if connection is up.
   * @return
   *   If is up
   */
  def testIO(implicit connPostgresModDB: ConnPostgresMod.DB, connMongoModDB: ConnMongoMod.DB): IO[Boolean] =
    `type` match {
      case ConnTypeEnum.Postgres =>
        for {
          conn <- connPostgresModDB.select(fr"conn_id = $id").map(_.head)
          isUp <- conn.testIO
        } yield isUp
      case ConnTypeEnum.Mongo    =>
        for {
          conn <- connMongoModDB.select(fr"conn_id = $id").map(_.head)
          isUp <- conn.testIO
        } yield isUp
    }

}

/**
 * [[ConnMod]] additions.
 */
object ConnMod {
  trait DB extends GenericDB[ConnMod] {

    /**
     * Constructor of [[ConnMod]].
     * @param userId
     *   User id
     * @param form
     *   Connection form
     * @return
     *   A new created connection
     */
    def apply(userId: Long, form: ConnFormIDto)(implicit
        connPostgresModDB: ConnPostgresMod.DB,
        connMongoModDB: ConnMongoMod.DB): IO[ConnMod] = form match {
      case form: ConnFormIDto.PostgresFormIDto =>
        for {
          conn <- insert(ConnMod(-1, userId, ConnTypeEnum.Postgres, form.name))
          _    <- connPostgresModDB(conn.id, form)
        } yield conn
      case form: ConnFormIDto.MongoFormIDto    =>
        for {
          conn <- insert(ConnMod(-1, userId, ConnTypeEnum.Mongo, form.name))
          _    <- connMongoModDB(conn.id, form)
        } yield conn
    }

  }
  object DB { implicit val impl: DB = new DB {} } // Auto-DI on import
}
