package com.ilovedatajjia
package models

import cats.effect.Clock
import cats.effect.IO
import doobie._
import doobie.implicits._
import io.circe.Encoder
import io.circe.Json
import java.sql.Timestamp
import java.util.UUID
import models.utils.Codec._
import models.utils.DBDriver._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration

/**
 * DB representation of a session.
 * @param id
 *   Session ID
 * @param authTokenSha1
 *   SHA-1 hashed authorization token
 */
case class Session(id: UUID,
                   authTokenSha1: String,
                   createdAt: Timestamp,
                   updatedAt: Timestamp,
                   deletedAt: Option[Timestamp]) {

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
  def persist: IO[Unit] = {
    // Build the query
    val query: ConnectionIO[Int] =
      sql"""INSERT INTO session (id, auth_token_sha1, created_at, updated_at, deleted_at)
           |VALUES ($id, $authTokenSha1, $createdAt, $updatedAt, $deletedAt)
           """.stripMargin.update.run

    // Compose IO
    for {
      nbAffectedRows <- mysqlDriver.use(query.transact(_))
      _              <- IO.raiseWhen(nbAffectedRows != 1)(
                          throw new RuntimeException(
                            s"Trying to persist session `$id` " +
                              s"causing table number of rows affected incoherence `$nbAffectedRows` != 1"))
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
      session <- Session.getWithId(id)

      // Update the cron job & session according the inactivity
      nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
      _            <- session.deletedAt match {
                        case None if nowTimestamp - session.updatedAt.getTime < maxDiffInactivity.toMillis  =>
                          startCronJobInactivityCheck() // Continue cron job if still active session
                        case None if nowTimestamp - session.updatedAt.getTime >= maxDiffInactivity.toMillis =>
                          Session.deleteWithId(id) // Terminate the cron job & Update to delete status the session
                        case _                                                                              =>
                          IO.unit // Do nothing if already in deletion state (== Some found)
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

  // For automatic class / JSON encoder
  implicit val sessionEncoder: Encoder[Session]                 = (session: Session) =>
    Json.obj(
      ("id", Json.fromString(session.id.toString)),
      ("createdAt", Json.fromString(session.getCreatedAt.toString)),
      ("updatedAt", Json.fromString(session.getUpdatedAt.toString)),
      ("deletedAt",
       session.getDeletedAt match {
         case None            => Json.Null
         case Some(deletedAt) => Json.fromString(deletedAt.toString)
       })
    )
  implicit def sessionEntityEncoder: EntityEncoder[IO, Session] =
    jsonEncoderOf[IO, Session] // useful when sending response

  /**
   * Constructor of [[Session]].
   * @param id
   *   Session ID
   * @param authToken
   *   Brut authorization token that will be hashed to SHA-1 hexadecimal string
   * @return
   *   A new created session
   */
  def apply(id: UUID, authToken: String): IO[Session] = for {
    nowTimestamp         <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    authTokenSha1: String = authToken.toSha1Hex // Hash with SHA1 the authorization token
  } yield Session(id, authTokenSha1, nowTimestamp, nowTimestamp, None)

  /**
   * Retrieve the session from the database. (Must only be used by the application logic)
   * @param id
   *   ID to find
   * @return
   *   The corresponding Session
   */
  def getWithId(id: UUID): IO[Session] = {
    // Build the query
    val query: ConnectionIO[Session] =
      sql"""SELECT id, auth_token_sha1, created_at, updated_at, deleted_at
           |FROM session
           |WHERE id=$id
           """.stripMargin.query[Session].unique // Will raise exception if not exactly one value

    // Run the query
    for {
      session <- mysqlDriver.use(query.transact(_))
    } yield session
  }

  /**
   * Retrieve the session from the database & Will throw exception if not found or deleted.
   * @param authToken
   *   Session with this authorization token to find
   * @return
   *   The corresponding Session
   */
  def getWithAuthToken(authToken: String): IO[Session] = {
    // Hash with SHA1 the authorization token
    val authTokenSha1: String = authToken.toSha1Hex

    // Build the query
    val query: ConnectionIO[Session] =
      sql"""SELECT id, auth_token_sha1, created_at, updated_at, deleted_at
           |FROM session
           |WHERE auth_token_sha1=$authTokenSha1;
           """.stripMargin.query[Session].unique // Will raise exception if not exactly one value

    // Run the query
    for {
      session <- mysqlDriver.use(query.transact(_))
      _       <- IO.raiseWhen(session.getDeletedAt.isDefined)(
                   throw new RuntimeException(s"Session id == `${session.id}` already terminated impossible to retrieve"))
    } yield session
  }

  /**
   * Refresh the session activity status in the database & Will throw exception if not found.
   * @param authToken
   *   Session with this authorization token to find
   */
  def refreshWithAuthToken(authToken: String): IO[Unit] = for {
    // Build the query
    nowTimestamp            <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    authTokenSha1: String    = authToken.toSha1Hex
    query: ConnectionIO[Int] =
      sql"""UPDATE session
             |SET updated_at=$nowTimestamp
             |WHERE auth_token_sha1=$authTokenSha1;
             """.stripMargin.update.run

    // Run the query (Raise exception if not exactly one value updated)
    nbAffectedRows          <- mysqlDriver.use(query.transact(_))
    _                       <- IO.raiseWhen(nbAffectedRows != 1)(
                                 throw new RuntimeException(
                                   s"Trying to persist session with auth_token_sha1 == `$authTokenSha1` " +
                                     s"causing table number of rows affected `$nbAffectedRows` != 1"))
  } yield ()

  /**
   * Delete the session in the database. (Must only be used by the application logic)
   * @param id
   *   ID to delete
   */
  def deleteWithId(id: UUID): IO[Unit] = for {
    // Build the query
    nowTimestamp            <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    query: ConnectionIO[Int] =
      sql"""UPDATE session
           |SET deleted_at=$nowTimestamp
           |WHERE id=$id
            """.stripMargin.update.run

    // Run the query
    nbAffectedRows          <- mysqlDriver.use(query.transact(_))
    _                       <- IO.raiseWhen(nbAffectedRows != 1)(
                                 throw new RuntimeException(
                                   s"Trying to persist session `$id` " +
                                     s"causing table number of rows affected `$nbAffectedRows` != 1"))
  } yield ()

}
