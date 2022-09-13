package com.ilovedatajjia
package models.job

import cats.effect.Clock
import cats.effect.IO
import doobie._
import doobie.implicits._
import java.sql.Timestamp
import models.job.Job.JobStatus._
import models.job.Job.JobType._
import models.utils.DBDriver.mysqlDriver

/**
 * DB representation of a job.
 * @param id
 *   Job ID
 * @param sessionId
 *   Corresponding session ID the job is running on
 * @param jobType
 *   Preview Job or Analyze Job
 * @param status
 *   If terminated or not
 * @param createdAt
 *   Job creation timestamp
 * @param terminatedAt
 *   Job termination timestamp
 */
case class Job(id: Long,
               sessionId: Long,
               jobType: JobType,
               status: JobStatus,
               createdAt: Timestamp,
               terminatedAt: Option[Timestamp])

/**
 * Additional [[Job]] functions.
 */
object Job {

  /**
   * Possible job types.
   */
  object JobType extends Enumeration {
    type JobType = Value
    val Preview, Analyze: JobType = Value
  }

  /**
   * Possible job status.
   */
  object JobStatus extends Enumeration {
    type JobStatus = Value
    val Starting, Running, Terminated: JobStatus = Value
  }

  /**
   * Constructor of [[Job]].
   * @param sessionId
   *   Corresponding session ID the job is running on
   * @param jobType
   *   Preview Job or Analyze Job ([[JobType]])
   * @return
   *   A new created job
   */
  def apply(sessionId: Long, jobType: JobType): IO[Job] = for {
    // Prepare the query
    nowTimestamp                <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    jobStatus: JobStatus         = Starting
    jobQuery: ConnectionIO[Long] =
      sql"""|INSERT INTO job (session_id, job_type, status, created_at)
            |VALUES ($sessionId, ${jobType.toString}, ${jobStatus.toString}, $nowTimestamp)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    jobId                       <- mysqlDriver.use(jobQuery.transact(_))
  } yield Job(jobId, sessionId, jobType, jobStatus, nowTimestamp, None)

}
