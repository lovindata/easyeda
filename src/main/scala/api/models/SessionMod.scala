package com.ilovedatajjia
package api.models

import api.helpers.AppLayerException
import api.helpers.AppLayerException.ModelLayerException
import api.helpers.CatsEffectExtension._
import api.helpers.CodecExtension._
import api.models.SessionMod._
import api.models.SessionStateEnum._
import api.models.SessionStateEnum.SessionStateType
import cats.data.EitherT
import cats.effect.Clock
import cats.effect.IO
import config.ConfigLoader.maxInactivity
import config.DBDriver.redisDriver
import io.circe._
import io.circe.generic.semiauto._
import java.sql.Timestamp
import org.http4s.Status
import redis.clients.jedis.UnifiedJedis
import redis.clients.jedis.json._
import redis.clients.jedis.search._
import redis.clients.jedis.search.IndexDefinition.Type
import scala.concurrent.duration.FiniteDuration
import scala.jdk.CollectionConverters._
import scala.util.Try

/**
 * DB representation of a session.
 * @param id
 *   Session ID
 * @param createdAt
 *   Session creation timestamp
 * @param updatedAt
 *   Session update timestamp
 * @param terminatedAt
 *   Session termination timestamp
 */
case class SessionMod(id: Long, createdAt: Timestamp, updatedAt: Timestamp, terminatedAt: Option[Timestamp]) {

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
   *   - exception from [[getWithId]]
   */
  def refreshStatus: EitherT[IO, AppLayerException, SessionMod] = for {
    nowTimestamp    <- EitherT.right(Clock[IO].realTime.map(_.toMillis))
    _               <- EitherT(redisDriver.use(x =>
                         IO {
                           val repStatus: String =
                             x.jsonSet(dataKey(id), new Path("updatedAt"), nowTimestamp, new JsonSetParams().xx)
                           repStatus match {
                             case "OK" => Right(())
                             case _    =>
                               Left(
                                 ModelLayerException(msgServer = "Corrupted session, `updatedAt` refreshing status field missing",
                                                     statusCodeServer = Status.BadGateway))
                           }
                         }))
    upToDateSession <- getWithId(id)
  } yield upToDateSession

  /**
   * Terminate the session in the database.
   * @return
   *   The up-to-date session OR
   *   - [[ModelLayerException]] if already terminated session
   *   - exception from [[getWithId]]
   */
  def terminate: EitherT[IO, AppLayerException, SessionMod] = for {
    nowTimestamp    <- EitherT.right(Clock[IO].realTime.map(_.toMillis))
    _               <-
      EitherT(redisDriver.use(x =>
        IO.blocking {
          val repStatus: String =
            x.jsonSet(dataKey(id), new Path("terminatedAt"), nowTimestamp, new JsonSetParams().nx)
          repStatus match {
            case "OK" => Right(())
            case _    =>
              Left(ModelLayerException(msgServer = "Already terminated session", statusCodeServer = Status.BadGateway))
          }
        }))
    upToDateSession <- getWithId(id)
  } yield upToDateSession

}

/**
 * Additional [[SessionMod]] functions.
 */
object SessionMod {

  // Global fixed variable(s)
  private val rootName: String        = "SessionMod"
  private val autoIdIncKey: String    = s"$rootName:AutoId"
  private val idsKey: String          = s"$rootName:Ids"
  private val dataKey: Long => String = id => s"$rootName:$id"

  // RedisSearch index(es)
  private val terminatedAtIndex: UnifiedJedis => IO[String] = x =>
    IO.blocking {
      val terminatedAtIndexName: String = s"$rootName:TerminatedAtIdx"
      Try(x.ftInfo(terminatedAtIndexName)).getOrElse( // Checks if it exists otherwise create one
        x.ftCreate(
          terminatedAtIndexName,
          IndexOptions.defaultOptions.setDefinition(new IndexDefinition(Type.JSON).setPrefixes(s"$rootName:")),
          new Schema()
            .addNumericField("$.terminatedAt")
            .as("terminatedAt")
        ))
      terminatedAtIndexName
    }

  // JSON (de)serializers (`Timestamp` saved as `Long` == Unix timestamp)
  private implicit val encTimestamp: Encoder[Timestamp] = Encoder.instance(x => Json.fromLong(x.getTime))
  private implicit val decTimestamp: Decoder[Timestamp] = Decoder.instance(_.as[Long].map(x => new Timestamp(x)))
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

    // Run & Get the created session
    id        <- redisDriver.use(x => IO.blocking(x.incr(autoIdIncKey)))
    _         <- redisDriver.use(x => IO.blocking(x.hset(idsKey, authTokenSha1, id.toString)))
    newSession = SessionMod(id, nowTimestamp, nowTimestamp, None)
    _         <-
      redisDriver.use(x =>
        IO.blocking(
          x.jsonSetWithPlainString(dataKey(id), Path.ROOT_PATH, encSessionMod(newSession).deepDropNullValues.noSpaces)))
  } yield newSession

  /**
   * Retrieve the session from the database. (Must only be used by the application logic)
   * @param id
   *   Session with the ID to retrieve
   * @return
   *   The corresponding Session OR
   *   - [[ModelLayerException]] if non retrievable session with provided ID
   */
  private def getWithId(id: Long): EitherT[IO, AppLayerException, SessionMod] = for {
    sessionStr <- EitherT.right(redisDriver.use(x => IO.blocking(x.jsonGetAsPlainString(dataKey(id), Path.ROOT_PATH))))
    session    <- EitherT(IO(parser.parse(sessionStr).flatMap(_.as[SessionMod]))).leftMap(e =>
                    ModelLayerException(
                      msgServer = s"Session not retrievable or parsable into model object with the implicitly provided ID",
                      overHandledException = Some(e),
                      statusCodeServer = Status.BadGateway
                    ))
  } yield session

  /**
   * Retrieve the session from the database & Will throw exception if not found or terminated.
   * @param authToken
   *   Session with this authorization token to find
   * @return
   *   The corresponding Session OR
   *   - exception from [[getWithId]]
   */
  def getWithAuthToken(authToken: String): EitherT[IO, AppLayerException, SessionMod] = for {
    sessionId <-
      EitherT(
        redisDriver.use(x =>
          IO.blocking(
            Option(x.hget(idsKey, authToken.toSha1Hex)) match { // Using `Option` to manage `null` value from `_.hget`
              case None                 =>
                Left(ModelLayerException("Authentication token provided incorrect, no corresponding session ID found"))
              case Some(sessionIdFound) => Right(sessionIdFound)
            })))
    session   <- getWithId(sessionId.toLong)
  } yield session

  /**
   * List all retrievable active sessions.
   * @param sessionState
   *   Filter according [[SessionStateType]]
   * @return
   *   List of sessions
   */
  def listSessions(sessionState: Option[SessionStateType]): IO[List[SessionMod]] = for {
    // Create if necessary the index
    terminatedAtIndexName <- redisDriver.use(terminatedAtIndex)

    // Start retrieving
    query          = sessionState match {
                       case None             => new Query("*")
                       case Some(Active)     => new Query("-(@terminatedAt:[-inf +inf])")
                       case Some(Terminated) => new Query("@terminatedAt:[-inf +inf]")
                       case Some(x)          =>
                         throw ModelLayerException(
                           s"Unhandled session state $x when building query"
                         ) // This case should never happened normally
                     }
    sessions      <-
      redisDriver.use(x =>
        IO.blocking(
          x.ftSearch(terminatedAtIndexName, query)
            .getDocuments
            .asScala
            .map(
              _.getProperties.asScala.head.getValue.toString // `_.head` is where the JSON is stored & `_.getValue.toString` to retrieve it
            )
        ))

    // Keep only the parsable
    sessionsParsed = sessions.map(x => parser.parse(x).flatMap(_.as[SessionMod])).collect { case Right(x) => x }.toList
  } yield sessionsParsed

}
