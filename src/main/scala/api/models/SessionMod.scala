package com.ilovedatajjia
package api.models

import api.helpers.AppLayerException
import api.helpers.AppLayerException.ModelLayerException
import api.helpers.CodecExtension._
import api.helpers.SessionStateEnum._
import api.models.SessionMod._
import cats.data.EitherT
import cats.effect._
import cats.effect.IO
import cats.implicits._
import config.ConfigLoader.maxInactivity
import config.DBDriver.redisDriver
import io.circe._
import io.circe.generic.semiauto._
import java.sql.Timestamp
import org.http4s.Status
import redis.clients.jedis.json._
import scala.concurrent.duration.FiniteDuration
import scala.jdk.CollectionConverters._

/**
 * DB representation of a session.
 * @param id
 *   Session ID // * @param authTokenSha1 // * SHA-1 hashed authorization token
 * @param createdAt
 *   Session creation timestamp
 * @param updatedAt
 *   Session update timestamp
 * @param terminatedAt
 *   Session termination timestamp
 */
case class SessionMod(id: Long,
                      // authTokenSha1: String,
                      createdAt: Timestamp,
                      updatedAt: Timestamp,
                      terminatedAt: Option[Timestamp]) {

  /*
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
   */

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
      sessionOrError <- SessionMod.getWithId(id).value

      // Update the cron job & session according the inactivity
      nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
      _            <- sessionOrError match {
                        case Right(SessionMod(_, _, sessionUpdatedAt, None))
                            if nowTimestamp - sessionUpdatedAt.getTime < maxInactivity.toMillis =>
                          startCronJobInactivityCheck() // Continue cron job if still active session
                        case Right(SessionMod(_, _, sessionUpdatedAt, None))
                            if nowTimestamp - sessionUpdatedAt.getTime >= maxInactivity.toMillis =>
                          this.terminate.value.void // Terminate the session
                        case _ =>
                          IO.unit // Do nothing if already in terminated state (== Some found) OR session get error
                      }
    } yield ()

    // Unblocking start of the cron job
    cronJobToStart.start.void
  }

  /**
   * Refresh the session activity status in the database.
   * @return
   *   The up-to-date session OR
   *   - [[ModelLayerException]] missing field [[updatedAt]]
   *   - under called exception [[getWithId]]
   */
  def refreshStatus: EitherT[IO, AppLayerException, SessionMod] = for {
    nowTimestamp    <- EitherT.right(Clock[IO].realTime.map(x => new Timestamp(x.toMillis)))
    _               <- EitherT(redisDriver.use(x =>
                         IO {
                           val repStatus: String =
                             x.jsonSet(sessionModKey(id), new Path("updatedAt"), nowTimestamp.toString, new JsonSetParams().xx)
                           repStatus match {
                             case "OK" => Right(())
                             case _    =>
                               Left(
                                 ModelLayerException(msgServer = "Corrupted session, update at refreshing status field missing",
                                                     statusCodeServer = Status.BadGateway))
                           }
                         }))
    upToDateSession <- getWithId(id)
  } yield upToDateSession

  /*
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
   */

  /**
   * Terminate the session in the database.
   * @return
   *   The up-to-date session OR
   *   - [[ModelLayerException]] if already terminated session
   *   - under called exception [[getWithId]]
   */
  def terminate: EitherT[IO, AppLayerException, SessionMod] = for {
    nowTimestamp    <- EitherT.right(Clock[IO].realTime.map(x => new Timestamp(x.toMillis)))
    _               <-
      EitherT(redisDriver.use(x =>
        IO.blocking {
          val repStatus: String =
            x.jsonSet(sessionModKey(id), new Path("terminatedAt"), nowTimestamp.toString, new JsonSetParams().nx)
          repStatus match {
            case "OK" => Right(())
            case _    =>
              Left(ModelLayerException(msgServer = "Already terminated session", statusCodeServer = Status.BadGateway))
          }
        }))
    upToDateSession <- getWithId(id)
  } yield upToDateSession

  /*
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
   */

}

/**
 * Additional [[SessionMod]] functions.
 */
object SessionMod {

  // Global fixed variable(s)
  private val autoIdIncKey: String          = "SessionMod:AutoId"
  private val authTokToIdKey: String        = "SessionMod:AuthTokToId"
  private val sessionModKey: Long => String = id => s"SessionMod:$id"

  // JSON (de)serializers
  private implicit val encTimestamp: Encoder[Timestamp] = Encoder.instance(x => Json.fromString(x.toString))
  private implicit val decTimestamp: Decoder[Timestamp] = Decoder.instance(_.as[String].map(Timestamp.valueOf))
  implicit val encSessionMod: Encoder[SessionMod]       = deriveEncoder
  implicit val decSessionMod: Decoder[SessionMod]       = deriveDecoder

  /**
   * Constructor of [[SessionMod]].
   * @param authToken
   *   Brut authorization token that will be hashed to SHA-1 hexadecimal string
   * @return
   *   A new created session
   */
  def apply(authToken: String): IO[SessionMod] = for {
    // Prepare the query
    nowTimestamp         <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    authTokenSha1: String = authToken.toSha1Hex // Hash with SHA1 the authorization token

    // Run & Get the auto-incremented session ID
    id        <- redisDriver.use(x => IO.blocking(x.incr(autoIdIncKey)))
    _         <- redisDriver.use(x => IO.blocking(x.hset(authTokToIdKey, authTokenSha1, id.toString)))
    newSession = SessionMod(id, nowTimestamp, nowTimestamp, None)
    _         <- redisDriver.use(x =>
                   IO.blocking(x.jsonSetWithPlainString(sessionModKey(id), Path.ROOT_PATH, encSessionMod(newSession).noSpaces)))
  } yield newSession

  /*
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
   */

  /**
   * Retrieve the session from the database. (Must only be used by the application logic)
   * @param id
   *   Session with the ID to retrieve
   * @return
   *   The corresponding Session OR
   *   - [[ModelLayerException]] if non retrievable session with provided ID
   */
  private def getWithId(id: Long): EitherT[IO, AppLayerException, SessionMod] = EitherT(for {
    sessionStr       <- redisDriver.use(x => IO.blocking(x.jsonGetAsPlainString(sessionModKey(id), Path.ROOT_PATH)))
    sessionException <- IO(parser.parse(sessionStr).flatMap(_.as[SessionMod]))
  } yield sessionException match {
    case Left(e)        =>
      Left(
        ModelLayerException(
          msgServer = s"Session not retrievable or parsable into model object with the implicitly provided ID",
          overHandledException = Some(e),
          statusCodeServer = Status.BadGateway
        ))
    case Right(session) => Right(session)
  })

  /*
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
   */

  /**
   * Retrieve the session from the database & Will throw exception if not found or terminated.
   * @param authToken
   *   Session with this authorization token to find
   * @return
   *   The corresponding Session OR
   *   - under called exception [[getWithId]]
   */
  def getWithAuthToken(authToken: String): EitherT[IO, AppLayerException, SessionMod] = for {
    sessionId <- EitherT.right(redisDriver.use(x => IO.blocking(x.hget(authTokToIdKey, authToken.toSha1Hex))))
    session   <- getWithId(sessionId.toLong)
  } yield session

  /*
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
   */

  /**
   * List all retrievable active sessions.
   * @param sessionState
   *   Filter according [[SessionStateType]]
   * @return
   *   List of sessions
   */
  def listSessions(sessionState: Option[SessionStateType]): IO[List[SessionMod]] = for {
    sessionIds <- redisDriver.use(x => IO.blocking(x.hvals(authTokToIdKey).asScala.toList))
    sessions   <- sessionIds.parTraverseFilter(x =>
                    getWithId(x.toLong).toOption.value.map { // 👈 From EitherT to OptionT for filtering
                      // Keep only if
                      //  - Retrievable && Not terminated && Active wanted
                      //  - Retrievable && Terminated && Terminated wanted
                      //  - Retrievable && All wanted
                      case Some(sessionSuccessfullyRetrieved)
                          if sessionSuccessfullyRetrieved.terminatedAt.isEmpty && sessionState.contains(Active) =>
                        Some(sessionSuccessfullyRetrieved)
                      case Some(sessionSuccessfullyRetrieved)
                          if sessionSuccessfullyRetrieved.terminatedAt.isDefined && sessionState.contains(Terminated) =>
                        Some(sessionSuccessfullyRetrieved)
                      case Some(sessionSuccessfullyRetrieved) if sessionState.isEmpty =>
                        Some(sessionSuccessfullyRetrieved)
                      // Else do not keep
                      case _ => None
                    })
  } yield sessions

  /*
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
   */

}
