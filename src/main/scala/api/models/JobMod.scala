package com.ilovedatajjia
package api.models

import api.helpers.AppLayerException
import api.helpers.AppLayerException.ModelLayerException
import api.helpers.CatsEffectExtension._
import api.models.JobMod._
import api.models.JobStatusEnum._
import api.models.JobStatusEnum.JobStatus
import cats.data.EitherT
import cats.effect.Clock
import cats.effect.IO
import config.DBDriver.redisDriver
import io.circe._
import io.circe.generic.semiauto._
import java.sql.Timestamp
import org.http4s.Status
import redis.clients.jedis.UnifiedJedis
import redis.clients.jedis.json._
import redis.clients.jedis.search._
import redis.clients.jedis.search.IndexDefinition.Type
import scala.util.Try

/**
 * DB representation of a job.
 * @param id
 *   Job ID
 * @param sessionId
 *   Corresponding session ID the job is running on
 * @param jobStatus
 *   If terminated or not
 * @param inputs
 *   JSON representation of the input(s)
 * @param outputs
 *   JSON representation of the output(s)
 * @param createdAt
 *   Job creation timestamp
 * @param terminatedAt
 *   Job termination timestamp
 */
case class JobMod(id: Long,
                  sessionId: Long,
                  jobStatus: JobStatus,
                  inputs: Json,
                  outputs: Option[Json],
                  createdAt: Timestamp,
                  terminatedAt: Option[Timestamp]) {

  /**
   * Terminate the job with saving the output and give back the new updated job.
   * @param output
   *   Job result saved as [[Json]]
   * @return
   *   Up to date [[JobMod]] OR
   *   - [[ModelLayerException]] if corrupted [[JobMod]] in the database
   *   - exception from [[getWithId]]
   */
  def terminate(output: Either[Json, Json]): EitherT[IO, AppLayerException, JobMod] = for {
    // Do the update
    nowTimestamp <- EitherT.right(Clock[IO].realTime.map(_.toMillis))
    _            <-
      EitherT(redisDriver.use(x =>
        IO.blocking {
          // Terminate the job by updating the values in database
          val (outputJson, jobStatus)       = output match {
            case Left(x)  => (x, encJobStatus(Failed).noSpaces)
            case Right(x) => (x, encJobStatus(Succeeded).noSpaces)
          }
          val repUpdateJobStatus: String    =
            x.jsonSet(dataKey(id), new Path("jobStatus"), jobStatus, new JsonSetParams().xx)
          val repUpdateTerminatedAt: String =
            x.jsonSet(dataKey(id), new Path("terminatedAt"), nowTimestamp, new JsonSetParams().nx)
          val repUpdateOutputs: String      =
            x.jsonSet(dataKey(id), new Path("outputs"), outputJson.noSpaces, new JsonSetParams().nx)

          // Verify coherence
          (repUpdateJobStatus, repUpdateTerminatedAt, repUpdateOutputs) match {
            case ("OK", "OK", "OK") => Right(())
            case _                  =>
              Left(
                ModelLayerException(
                  s"Job `$id` corrupted, at least one of the fields was not `OK` when updating" +
                    s" (`(jobStatus, terminatedAt, outputs) == ($repUpdateJobStatus, $repUpdateTerminatedAt, $repUpdateOutputs)`",
                  statusCodeServer = Status.BadGateway
                ))
          }
        }))

    // Retrieve the up to date
    upToDateJob  <- getWithId(id)
  } yield upToDateJob

}

/**
 * Additional [[JobMod]] functions.
 */
object JobMod {

  // Global fixed variable(s)
  private val rootName: String        = "JobMod"
  private val autoIdIncKey: String    = s"$rootName:AutoId"
  private val dataKey: Long => String = id => s"$rootName:$id"

  // RedisSearch index(es)
  private val sessionIdIndex: UnifiedJedis => IO[String] = x =>
    IO.blocking {
      val sessionIdIndexName: String = s"$rootName:SessionIdIdx"
      Try(x.ftInfo(sessionIdIndexName)).getOrElse( // Checks if it exists otherwise create one
        x.ftCreate(
          sessionIdIndexName,
          IndexOptions.defaultOptions.setDefinition(new IndexDefinition(Type.JSON).setPrefixes(s"$rootName:")),
          new Schema()
            .addNumericField("$.sessionId")
            .as("sessionId")
        ))
      sessionIdIndexName
    }

  // JSON (de)serializers
  private implicit val encTimestamp: Encoder[Timestamp] = Encoder.instance(x => Json.fromLong(x.getTime))
  private implicit val decTimestamp: Decoder[Timestamp] = Decoder.instance(_.as[Long].map(x => new Timestamp(x)))
  implicit val encJobMod: Encoder[JobMod]               = deriveEncoder
  implicit val decJobMod: Decoder[JobMod]               = deriveDecoder

  /**
   * Constructor of [[JobMod]].
   * @param sessionId
   *   Corresponding session ID the job is running on
   * @param inputs
   *   Input used for the job
   * @return
   *   A new created job
   */
  def apply(sessionId: Long, inputs: Json): IO[JobMod] = for {
    // Prepare the query
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))

    // Run & Get the created job
    id           <- redisDriver.use(x => IO.blocking(x.incr(autoIdIncKey)))
    newCreatedJob = JobMod(id, sessionId, Running, inputs, None, nowTimestamp, None)
    _            <- redisDriver.use(x =>
                      IO.blocking(x.jsonSetWithPlainString(dataKey(id), Path.ROOT_PATH, encJobMod(newCreatedJob).noSpaces)))
  } yield newCreatedJob

  /**
   * Retrieve a [[JobMod]] with an ID.
   * @param id
   *   ID of the [[JobMod]] to retrieve
   * @return
   *   Retrieved [[JobMod]] OR
   *   - [[ModelLayerException]] if non existing or incoherent job
   */
  private def getWithId(id: Long): EitherT[IO, AppLayerException, JobMod] = for {
    jobStr <- EitherT.right(redisDriver.use(x => IO.blocking(x.jsonGetAsPlainString(dataKey(id), Path.ROOT_PATH))))
    job    <- EitherT(IO(parser.parse(jobStr).flatMap(_.as[JobMod]))).leftMap(e =>
                ModelLayerException(
                  msgServer = s"Job not retrievable or parsable into model object with the implicitly provided ID",
                  overHandledException = Some(e),
                  statusCodeServer = Status.BadGateway
                ))
  } yield job

}
