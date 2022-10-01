package com.ilovedatajjia
package api.models

import api.helpers.Codec._
import api.helpers.SessionStateEnum._
import api.models.SessionMod._
import cats.effect.Clock
import cats.effect.IO
import config.ConfigLoader.maxInactivity
import config.DBDriver._
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import java.sql.Timestamp
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
case class SessionMod(id: Long,
                      authTokenSha1: String,
                      createdAt: Timestamp,
                      updatedAt: Timestamp,
                      terminatedAt: Option[Timestamp]) {

  /**
   * Throw exception when not unique session modified.
   * @param nbRowsAffected
   *   Number of rows affected when querying
   */
  private def ensureUniqueModification(nbRowsAffected: Int): IO[Unit] = for {
    _ <- IO.raiseWhen(nbRowsAffected == 0)(
           throw new RuntimeException(
             s"Trying to update a non-existing session " +
               s"with id == `$id` (`nbAffectedRows` == 0)")
         )
    _ <- IO.raiseWhen(nbRowsAffected >= 2)(
           throw new RuntimeException(
             s"Updated multiple session with unique id == `$id` " +
               s"(`nbAffectedRows` != $nbRowsAffected, session table might be corrupted)"))
  } yield ()

  /**
   * Launch the cron job checking inactivity status. If session inactive the cron job will terminate & updated in the
   * database. (The session is supposed existing in the database)
   * @param maxInactivity
   *   Duration to be consider inactive after
   */
  def startCronJobInactivityCheck(maxInactivity: FiniteDuration = maxInactivity): IO[Unit] = {
    // Define the cron job
    val cronJobToStart: IO[Unit] = for {
      // Scheduled every certain amount of time
      _ <- IO.sleep(maxInactivity)

      // Retrieve the actual session state from the database
      session <- SessionMod.getWithId(id)

      // Update the cron job & session according the inactivity
      nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
      _            <- session.terminatedAt match {
                        case None if nowTimestamp - session.updatedAt.getTime < maxInactivity.toMillis  =>
                          startCronJobInactivityCheck() // Continue cron job if still active session
                        case None if nowTimestamp - session.updatedAt.getTime >= maxInactivity.toMillis =>
                          this.terminate // Terminate the cron job & Update to terminated status the session
                        case _                                                                          =>
                          IO.unit // Do nothing if already in terminated state (== Some found)
                      }
    } yield ()

    // Unblocking start of the cron job
    cronJobToStart.start.void
  }

  /**
   * Refresh the session activity status in the database.
   * @return
   *   The up-to-date session
   */
  def refreshStatus: IO[SessionMod] = for {
    // Build the query
    nowTimestamp            <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    query: ConnectionIO[Int] =
      sql"""|UPDATE session
            |SET updated_at=$nowTimestamp, terminated_at=NULL
            |WHERE id=$id
            |""".stripMargin.update.run

    // Run the query (Raise exception if not exactly one value updated)
    nbAffectedRows          <- postgresDriver.use(query.transact(_))
    _                       <- ensureUniqueModification(nbAffectedRows)

    // Retrieve the up to date session
    upToDateSession <- getWithId(id)
  } yield upToDateSession

  /**
   * Terminate the session in the database.
   * @return
   *   The up-to-date session
   */
  def terminate: IO[SessionMod] = for {
    // Build the query
    nowTimestamp            <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    query: ConnectionIO[Int] =
      sql"""|UPDATE session
            |SET terminated_at=$nowTimestamp
            |WHERE id=$id
            |""".stripMargin.update.run

    // Run the query
    nbAffectedRows          <- postgresDriver.use(query.transact(_))
    _                       <- ensureUniqueModification(nbAffectedRows)

    // Retrieve the up to date session
    upToDateSession <- getWithId(id)
  } yield upToDateSession

}

/**
 * Additional [[SessionMod]] functions.
 */
object SessionMod {

  /**
   * Constructor of [[SessionMod]].
   * @param authToken
   *   Brut authorization token that will be hashed to SHA-1 hexadecimal string
   * @return
   *   A new created session
   */
  def apply(authToken: String): IO[SessionMod] = for {
    // Prepare the query
    nowTimestamp             <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    authTokenSha1: String     = authToken.toSha1Hex // Hash with SHA1 the authorization token
    query: ConnectionIO[Long] =
      sql"""|INSERT INTO session (bearer_auth_token_sha1, created_at, updated_at)
            |VALUES ($authTokenSha1, $nowTimestamp, $nowTimestamp)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented session ID
    id                       <- postgresDriver.use(query.transact(_))
  } yield SessionMod(id, authTokenSha1, nowTimestamp, nowTimestamp, None)

  /**
   * Retrieve the session from the database. (Must only be used by the application logic)
   * @param id
   *   ID to find
   * @return
   *   The corresponding Session
   */
  private def getWithId(id: Long): IO[SessionMod] = {
    // Build the query
    val query: ConnectionIO[SessionMod] =
      sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
            |FROM session
            |WHERE id=$id
            |""".stripMargin.query[SessionMod].unique // Will raise exception if not exactly one value

    // Run the query
    for {
      session <- postgresDriver.use(query.transact(_))
    } yield session
  }

  /**
   * Retrieve the session from the database & Will throw exception if not found or terminated.
   * @param authToken
   *   Session with this authorization token to find
   * @return
   *   The corresponding Session
   */
  def getWithAuthToken(authToken: String): IO[SessionMod] = {
    // Hash with SHA1 the authorization token
    val authTokenSha1: String = authToken.toSha1Hex

    // Build the query
    val query: ConnectionIO[SessionMod] =
      sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
            |FROM session
            |WHERE bearer_auth_token_sha1=$authTokenSha1
            |""".stripMargin.query[SessionMod].unique // Will raise exception if not exactly one value

    // Run the query
    for {
      session <- postgresDriver.use(query.transact(_))
    } yield session
  }

  /**
   * List all active sessions.
   * @return
   *   Array of all non terminated sessions
   */
  def listSessions(sessionState: Option[SessionStateType]): IO[Array[SessionMod]] = {
    // Build the query
    val query: ConnectionIO[Array[SessionMod]] = sessionState match {
      case Some(Active)     =>
        sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
              |FROM session
              |WHERE terminated_at IS NULL
              |""".stripMargin.query[SessionMod].to[Array]
      case Some(Terminated) =>
        sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
              |FROM session
              |WHERE terminated_at IS NOT NULL
              |""".stripMargin.query[SessionMod].to[Array]
      case _                =>
        sql"""|SELECT id, bearer_auth_token_sha1, created_at, updated_at, terminated_at
              |FROM session
              |""".stripMargin.query[SessionMod].to[Array]
    }

    // Run the query
    for {
      sessions <- postgresDriver.use(query.transact(_))
    } yield sessions
  }

}
