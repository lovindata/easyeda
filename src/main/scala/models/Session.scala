package com.ilovedatajjia
package models

import cats.effect.IO
import doobie._
import doobie.implicits._
import java.sql.Timestamp
import java.util.UUID
import models.utils._

/**
 * DB representation of a session.
 * @param id
 *   Session ID
 * @param authTokenSha1
 *   Hashed authorization header
 */
case class Session(id: UUID,
                   authTokenSha1: Array[Byte],
                   private val createdAt: Timestamp = new Timestamp(System.currentTimeMillis()),
                   private val updatedAt: Option[Timestamp] = None,
                   private val deletedAt: Option[Timestamp] = None) {

  /**
   * Get [[createdAt]].
   * @return
   *   String representation of [[createdAt]].
   */
  def getCreatedAt: String = createdAt.toString

  /**
   * Get [[updatedAt]].
   * @return
   *   String representation of [[updatedAt]].
   */
  def getUpdatedAt: String = updatedAt.toString

  /**
   * Get [[deletedAt]].
   * @return
   *   String representation of [[deletedAt]].
   */
  def getDeletedAt: String = deletedAt.toString

  /**
   * Add the new session to the database (Supposed not-existing inside the database).
   * @return
   *   An IO containing the execution
   */
  def persistSession: IO[Session] = {

    // Build the query
    val query: Update0 =
      sql"""INSERT INTO session (id, auth_token_sha1, created_at, updated_at, deleted_at)
           |VALUES ($id, $authTokenSha1, $createdAt, $updatedAt, $deletedAt)
           |""".stripMargin.update

    // Compose IO
    for {
      nbAffectedRows <- mysqlDriver.use(query.run.transact(_))
      _              <- IO.raiseWhen(nbAffectedRows != 1)(
                          throw new RuntimeException(
                            s"Incoherence in number of rows affected (= $nbAffectedRows) after trying to add $id"))
    } yield this

  }

}

/**
 * Additional [[Session]] functions.
 */
object Session {

  /**
   * Retrieve the session from the database.
   * @param id
   *   ID to find
   * @param authToken
   *   Authentication token to verify after retrieving
   * @return
   *   The corresponding Session
   */
  def getSession(id: UUID, authToken: String): IO[Session] = {

    // Build the query
    val query: doobie.ConnectionIO[Session] =
      sql"""SELECT id, auth_token_sha1, created_at, updated_at, deleted_at
            |FROM session
            |WHERE id==$id AND authToken==$authToken
            """.stripMargin.query[Session].unique // Will raise exception if not exactly one value

    // Compose IO
    for {
      session <- mysqlDriver.use(query.transact(_))
    } yield session

  }

}
