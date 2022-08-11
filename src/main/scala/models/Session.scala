package com.ilovedatajjia
package models

import cats.effect.Clock
import cats.effect.IO
import doobie._
import doobie.implicits._
import java.sql.Timestamp
import java.util.UUID
import models.utils._
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration

/**
 * DB representation of a session.
 * @param id
 *   Session ID
 * @param authTokenSha1
 *   Hashed authorization header
 */
case class Session(id: UUID,
                   authTokenSha1: String,
                   private val createdAt: Timestamp,
                   private val updatedAt: Timestamp,
                   private val deletedAt: Option[Timestamp]) {

  /**
   * Get [[createdAt]].
   * @return
   *   String representation of [[createdAt]].
   */
  def getCreatedAt: Timestamp = createdAt

  /**
   * Get [[updatedAt]].
   * @return
   *   String representation of [[updatedAt]].
   */
  def getUpdatedAt: Timestamp = updatedAt

  /**
   * Get [[deletedAt]].
   * @return
   *   String representation of [[deletedAt]].
   */
  def getDeletedAt: Option[Timestamp] = deletedAt

  /**
   * Add the new session to the database (Supposed not-existing inside the database).
   * @return
   *   An IO containing the execution
   */
  def persistSession: IO[Unit] = {

    // Build the query
    val query: Update0 =
      sql"""INSERT INTO session (id, auth_token_sha1, created_at, updated_at, deleted_at)
           |VALUES ($id, $authTokenSha1, $createdAt, $updatedAt, $deletedAt)
           """.stripMargin.update

    // Compose IO
    for {
      nbAffectedRows <- mysqlDriver.use(query.run.transact(_))
      _              <- IO.raiseWhen(nbAffectedRows != 1)(
                          throw new RuntimeException(
                            s"Trying to persist session $id " +
                              s"causing table number of rows affected incoherence $nbAffectedRows (!= 1)"))
    } yield ()

  }

  /**
   * Launch the cron job checking inactivity status. If session inactive the cron job will terminate & updated in the
   * database. (The session is supposed existing in the database)
   * @param maxDiffInactivity
   *   Duration to be consider inactive after
   */
  def startCronJobInactivityCheck(maxDiffInactivity: FiniteDuration = 1.minute): IO[Unit] = {

    // Define the cron job
    val cronJobToStart: IO[Unit] = for {

      // Scheduled every certain amount of time
      _ <- IO.sleep(maxDiffInactivity)

      // Retrieve the actual session state from the database
      session <- Session.getSession(id)

      // Update the cron job & session according the inactivity
      nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
      _            <- session.deletedAt match {
                        case None if nowTimestamp - session.updatedAt.getTime < maxDiffInactivity.toMillis  =>
                          startCronJobInactivityCheck() // Continue cron job if still active session
                        case None if nowTimestamp - session.updatedAt.getTime >= maxDiffInactivity.toMillis =>
                          Session.deleteSession(id) // Terminate the cron job & Update to delete status the session
                        case Some(_)                                                                        =>
                          IO.unit // Do nothing if already in deletion state
                      }

    } yield ()

    // Unblocking start of the cron job
    cronJobToStart.start.void

  }

}

/**
 * Additional [[Session]] functions.
 */
object Session {

  /**
   * Constructor of [[Session]] & Start a cron job inactivity check.
   * @param id
   *   Session ID
   * @param authTokenSha1
   *   Session SHA-1 hashed in hexadecimal authentication token
   * @return
   *   A new created session
   */
  def apply(id: UUID, authTokenSha1: String): IO[Session] = for {
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
  } yield Session(id, authTokenSha1, nowTimestamp, nowTimestamp, None)

  /**
   * Retrieve the session from the database.
   * @param id
   *   ID to find
   * @return
   *   The corresponding Session
   */
  def getSession(id: UUID): IO[Session] = {

    // Build the query
    val query: doobie.ConnectionIO[Session] =
      sql"""SELECT id, auth_token_sha1, created_at, updated_at, deleted_at
           |FROM session
           |WHERE id=$id
           """.stripMargin.query[Session].unique // Will raise exception if not exactly one value

    // Compose IO
    for {
      session <- mysqlDriver.use(query.transact(_))
    } yield session

  }

  /**
   * Delete the session in the database.
   * @param id
   *   ID to delete
   */
  def deleteSession(id: UUID): IO[Unit] = for {
    // Build the query
    nowTimestamp   <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    query: Update0  =
      sql"""UPDATE session
           |SET deleted_at=$nowTimestamp
           |WHERE id=$id
            """.stripMargin.update

    // Run the query
    nbAffectedRows <- mysqlDriver.use(query.run.transact(_))
    _              <- IO.raiseWhen(nbAffectedRows != 1)(
                        throw new RuntimeException(
                          s"Trying to persist session $id " +
                            s"causing table number of rows affected incoherence $nbAffectedRows (!= 1)"))
  } yield ()

}
