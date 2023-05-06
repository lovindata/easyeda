package com.ilovedatajjia
package api.models.conn

import api.dto.input.ConnFormIDto
import api.dto.output.ConnTestODto
import api.helpers.BackendException.AppException
import api.helpers.JdbcUtils
import api.models._
import cats.effect.IO
import cats.implicits._

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
   *   [[ConnTestODto]]
   */
  def testConn: IO[ConnTestODto] = JdbcUtils
    .testConn("org.postgresql.Driver", s"jdbc:postgresql://$host:$port/$dbName", "user" -> user, "password" -> pwd)
    .map { case (isUp, errMsg) => ConnTestODto(isUp, errMsg) }

  /**
   * List databases.
   * @return
   *   List of databases name OR
   *   - [[AppException]] if incoherent result set
   */
  def listDb: IO[List[String]] = for {
    resSet <-
      JdbcUtils.runSQL("org.postgresql.Driver",
                       s"jdbc:postgresql://$host:$port/$dbName",
                       "user"     -> user,
                       "password" -> pwd)("SELECT datname FROM pg_catalog.pg_database WHERE datistemplate = false;")
    dbs    <- resSet match {
                case None         => IO.raiseError(AppException("Implementation error, sql query with no result set."))
                case Some(resSet) =>
                  IO(
                    resSet.map(
                      _.head.getOrElse(throw AppException("Implementation error, sql query result set with null."))))
              }
  } yield dbs

  /**
   * List schemas for a given database.
   * @param db
   *   Database
   * @return
   *   List of schemas name OR
   *   - [[AppException]] if query error or incoherent result set
   */
  def listSch(db: String): IO[List[String]] = for {
    resSet  <- JdbcUtils
                 .runSQL(
                   "org.postgresql.Driver",
                   s"jdbc:postgresql://$host:$port/$db", // Use arg "db" because postgres database dependent connection
                   "user"     -> user,
                   "password" -> pwd
                 )("SELECT schema_name FROM information_schema.schemata;")
                 .attemptT // Wrap and rethrow in case of query error
                 .leftMap(t => AppException(t.getMessage))
                 .rethrowT
    schemas <- resSet match {
                 case Some(resSet) =>
                   IO(
                     resSet.map(
                       _.head.getOrElse(throw AppException("Implementation error, sql query result set with null."))))
                 case _            => IO.raiseError(AppException("Implementation error, sql query with incoherent result set."))
               }
  } yield schemas

  /**
   * List tables for a given database and schema.
   * @param db
   *   Database
   * @param sch
   *   Schema
   * @return
   *   List of tables name OR
   *   - [[AppException]] if query error or incoherent result set
   */
  def listTab(db: String, sch: String): IO[List[String]] = for {
    // Verify schema existence (because query does not manage it)
    schemas <- listSch(db)
    _       <- IO.raiseUnless(schemas.contains(sch))(AppException(s"ERROR: schema \"$sch\" does not exist"))

    // Do query
    resSet <- JdbcUtils
                .runSQL(
                  "org.postgresql.Driver",
                  s"jdbc:postgresql://$host:$port/$db", // Use arg "db" because postgres database dependent connection
                  "user"     -> user,
                  "password" -> pwd
                )(s"SELECT tablename FROM pg_catalog.pg_tables where schemaname = '$sch';")
                .attemptT // Wrap and rethrow in case of query error
                .leftMap(t => AppException(t.getMessage))
                .rethrowT
    tables <- resSet match {
                case Some(resSet) =>
                  IO(
                    resSet.map(
                      _.head.getOrElse(throw AppException("Implementation error, sql query result set with null."))))
                case _            => IO.raiseError(AppException("Implementation error, sql query with incoherent result set."))
              }
  } yield tables

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
