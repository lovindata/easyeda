package com.ilovedatajjia
package api.models

import api.dto.input.ConnFormIDto
import api.dto.output.ConnTestODto
import api.helpers.BackendException.AppException
import api.helpers.ConnTypeEnum
import api.helpers.DoobieUtils._
import api.models.conn._
import cats.effect._

/**
 * DB representation of connections.
 * @param id
 *   Connection id
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
   *   [[ConnTestODto]]
   */
  def testConn(implicit connPostgresModDB: PostgresMod.DB, connMongoModDB: MongoMod.DB): IO[ConnTestODto] =
    `type` match {
      case ConnTypeEnum.Postgres =>
        for {
          conn <- connPostgresModDB.select(fr"conn_id = $id").map(_.head)
          dto  <- conn.testConn
        } yield dto
      case ConnTypeEnum.Mongo    =>
        for {
          conn <- connMongoModDB.select(fr"conn_id = $id").map(_.head)
          dto  <- conn.testConn
        } yield dto
    }

  /**
   * Access postgres.
   * @return
   *   Postgres model OR
   *   - [[AppException]] if incoherent access
   */
  def postgres(implicit connPostgresModDB: PostgresMod.DB): IO[PostgresMod] = `type` match {
    case ConnTypeEnum.Postgres => connPostgresModDB.select(fr"conn_id = $id").map(_.head)
    case _                     => IO.raiseError(AppException(s"Accessing ${ConnTypeEnum.Postgres} but connection is of type ${`type`}."))
  }

  /**
   * Access mongodb.
   * @return
   *   MongoDB model OR
   *   - [[AppException]] if incoherent access
   */
  def mongo(implicit connMongoModDB: MongoMod.DB): IO[MongoMod] = `type` match {
    case ConnTypeEnum.Mongo => connMongoModDB.select(fr"conn_id = $id").map(_.head)
    case _                  => IO.raiseError(AppException(s"Accessing ${ConnTypeEnum.Mongo} but connection is of type ${`type`}."))
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
                                                connPostgresModDB: PostgresMod.DB,
                                                connMongoModDB: MongoMod.DB): IO[ConnMod] = form match {
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
