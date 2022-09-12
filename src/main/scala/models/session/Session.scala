package com.ilovedatajjia
package models.session

import cats.effect.Clock
import cats.effect.IO
import doobie._
import doobie.implicits._
import java.sql.Timestamp
import models.utils.Codec._
import models.utils.DBDriver._
import scala.concurrent.duration.DurationInt
import scala.concurrent.duration.FiniteDuration

/**
 * DB representation of a session.
 * @param id
 *   Session ID
 * @param authTokenSha1
 *   SHA-1 hashed authorization token
 * @param createdAt
 *   Session creation timestamp
 * @param updatedAt
 *   Session update timestamp
 * @param terminatedAt
 *   Session termination timestamp
 */
case class Session(id: Long,
                   authTokenSha1: String,
                   createdAt: Timestamp,
                   updatedAt: Timestamp,
                   terminatedAt: Option[Timestamp]) {

  /**
   * Launch the cron job checking inactivity status. If session inactive the cron job will terminate & updated in the
   * database. (The session is supposed existing in the database)
   * @param maxDiffInactivity
   *   Duration to be consider inactive after
   */
  def startCronJobInactivityCheck(maxDiffInactivity: FiniteDuration = 1.hour): IO[Unit] = {
    // Define the cron job
    val cronJobToStart: IO[Unit] = for {
      // Scheduled every certain amount of time
      _ <- IO.sleep(maxDiffInactivity)

      // Retrieve the actual session state from the database
      session <- Session.getWithId(id)

      // Update the cron job & session according the inactivity
      nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
      _            <- session.terminatedAt match {
                        case None if nowTimestamp - session.updatedAt.getTime < maxDiffInactivity.toMillis  =>
                          startCronJobInactivityCheck() // Continue cron job if still active session
                        case None if nowTimestamp - session.updatedAt.getTime >= maxDiffInactivity.toMillis =>
                          Session.terminateWithId(id) // Terminate the cron job & Update to terminated status the session
                        case _                                                                              =>
                          IO.unit // Do nothing if already in terminated state (== Some found)
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
   * Constructor of [[Session]].
   * @param authToken
   *   Brut authorization token that will be hashed to SHA-1 hexadecimal string
   * @return
   *   A new created session
   */
  def apply(authToken: String): IO[Session] = for {
    // Prepare the query
    nowTimestamp             <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    authTokenSha1: String     = authToken.toSha1Hex // Hash with SHA1 the authorization token
    query: ConnectionIO[Long] =
      sql"""|INSERT INTO session (bearer_auth_token_sha1, created_at, updated_at)
            |VALUES ($authTokenSha1, $nowTimestamp, $nowTimestamp)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented session ID
    id                       <- mysqlDriver.use(query.transact(_))
  } yield Session(id, authTokenSha1, nowTimestamp, nowTimestamp, None)

  /**
   * Retrieve the session from the database. (Must only be used by the application logic)
   * @param id
   *   ID to find
   * @return
   *   The corresponding Session
   */
  def getWithId(id: Long): IO[Session] = {
    // Build the query
    val query: ConnectionIO[Session] =
      sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
            |FROM session
            |WHERE id=$id
            |""".stripMargin.query[Session].unique // Will raise exception if not exactly one value

    // Run the query
    for {
      session <- mysqlDriver.use(query.transact(_))
    } yield session
  }

  /**
   * Retrieve the session from the database & Will throw exception if not found or terminated.
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
      sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
            |FROM session
            |WHERE bearer_auth_token_sha1=$authTokenSha1
            |""".stripMargin.query[Session].unique // Will raise exception if not exactly one value

    // Run the query
    for {
      session <- mysqlDriver.use(query.transact(_))
      _       <-
        IO.raiseWhen(session.terminatedAt.isDefined)(
          throw new RuntimeException(s"Session with id == `${session.id}` already terminated impossible to retrieve"))
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
      sql"""|UPDATE session
            |SET updated_at=$nowTimestamp
            |WHERE bearer_auth_token_sha1=$authTokenSha1
            |""".stripMargin.update.run

    // Run the query (Raise exception if not exactly one value updated)
    nbAffectedRows          <- mysqlDriver.use(query.transact(_))
    _                       <- IO.raiseWhen(nbAffectedRows == 0)(
                                 throw new RuntimeException(
                                   s"Trying to update a non-existing session " +
                                     s"with bearer_auth_token_sha1 == `$authTokenSha1` (`nbAffectedRows` == 0)")
                               )
    _                       <- IO.raiseWhen(nbAffectedRows >= 2)(
                                 throw new RuntimeException(
                                   s"Updated multiple session with unique bearer_auth_token_sha1 == `$authTokenSha1` " +
                                     s"(`nbAffectedRows` != $nbAffectedRows). Table might be corrupted."))
  } yield ()

  /**
   * Terminate the session in the database. (Must only be used by the application logic)
   * @param id
   *   ID to terminate
   */
  def terminateWithId(id: Long): IO[Unit] = for {
    // Build the query
    nowTimestamp            <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    query: ConnectionIO[Int] =
      sql"""|UPDATE session
            |SET terminated_at=$nowTimestamp
            |WHERE id=$id
            |""".stripMargin.update.run

    // Run the query
    nbAffectedRows          <- mysqlDriver.use(query.transact(_))
    _                       <- IO.raiseWhen(nbAffectedRows == 0)(
                                 throw new RuntimeException(
                                   s"Trying to update a non-existing session " +
                                     s"with id == `$id` (`nbAffectedRows` == 0)")
                               )
    _                       <- IO.raiseWhen(nbAffectedRows >= 2)(
                                 throw new RuntimeException(
                                   s"Updated multiple session with unique id == `$id` " +
                                     s"(`nbAffectedRows` != $nbAffectedRows). Table might be corrupted."))
  } yield ()

  /**
   * List all active sessions.
   * @return
   *   Array of all non terminated sessions
   */
  def listActiveSessions: IO[Array[Session]] = {
    // Build the query
    val query: ConnectionIO[Array[Session]] =
      sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
            |FROM session
            |WHERE terminated_at IS NULL
            |""".stripMargin.query[Session].to[Array]

    // Run the query
    for {
      sessions <- mysqlDriver.use(query.transact(_))
    } yield sessions
  }

}
