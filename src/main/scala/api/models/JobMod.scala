package com.ilovedatajjia
package api.models

import api.models.JobMod.JobStatus.JobStatus
import api.models.JobMod.JobType.JobType
import cats.effect.Clock
import cats.effect.IO
import doobie._
import doobie.implicits._
import java.sql.Timestamp
import services.DBDriver._

/**
 * DB representation of a job.
 * @param id
 *   Job ID
 * @param sessionId
 *   Corresponding session ID the job is running on
 * @param jobType
 *   Preview Job or Analyze Job
 * @param jobStatus
 *   If terminated or not
 * @param createdAt
 *   Job creation timestamp
 * @param terminatedAt
 *   Job termination timestamp
 */
case class JobMod(id: Long,
                  sessionId: Long,
                  jobType: JobType,
                  jobStatus: JobStatus,
                  createdAt: Timestamp,
                  terminatedAt: Option[Timestamp]) {

  /**
   * Update the [[jobStatus]] to running.
   */
  def toRunning: IO[Unit] = {
    // Build the query
    val query: ConnectionIO[Int] =
      sql"""|UPDATE job
            |SET job_status=${jobStatus.toString}
            |WHERE id=$id
            |""".stripMargin.update.run

    // Run the query
    for {
      nbAffectedRows <- mysqlDriver.use(query.transact(_))
      _              <- IO.raiseWhen(nbAffectedRows == 0)(
                          throw new RuntimeException(
                            s"Trying to update a non-existing job " +
                              s"with id == `$id` (`nbAffectedRows` == 0)")
                        )
      _              <- IO.raiseWhen(nbAffectedRows >= 2)(
                          throw new RuntimeException(
                            s"Updated multiple job with unique id == `$id` " +
                              s"(`nbAffectedRows` != $nbAffectedRows). Table might be corrupted."))
    } yield ()
  }

  /**
   * Update the [[jobStatus]] to terminated.
   */
  def toTerminated: IO[Unit] = {
    // Build the query
    val query: ConnectionIO[Int] =
      sql"""|UPDATE job
            |SET job_status=${jobStatus.toString}
            |WHERE id=$id
            |""".stripMargin.update.run

    // Run the query
    for {
      nbAffectedRows <- mysqlDriver.use(query.transact(_))
      _              <- IO.raiseWhen(nbAffectedRows == 0)(
                          throw new RuntimeException(
                            s"Trying to update a non-existing job " +
                              s"with id == `$id` (`nbAffectedRows` == 0)")
                        )
      _              <- IO.raiseWhen(nbAffectedRows >= 2)(
                          throw new RuntimeException(
                            s"Updated multiple job with unique id == `$id` " +
                              s"(`nbAffectedRows` != $nbAffectedRows). Table might be corrupted."))
    } yield ()
  }

}

/**
 * Additional [[JobMod]] functions.
 */
object JobMod {

  /**
   * Possible job types.
   */
  object JobType extends Enumeration {
    type JobType = Value
    val Preview, Full: JobType = Value
  }

  /**
   * Possible job status.
   */
  object JobStatus extends Enumeration {
    type JobStatus = Value
    val Starting, Running, Terminated: JobStatus = Value
  }

  /**
   * Constructor of [[JobMod]].
   * @param sessionId
   *   Corresponding session ID the job is running on
   * @param jobType
   *   Preview Job or Analyze Job ([[JobType]])
   * @return
   *   A new created job
   */
  def apply(sessionId: Long, jobType: JobType): IO[JobMod] = for {
    // Prepare the query
    nowTimestamp                <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    jobStatus: JobStatus         = JobStatus.Starting
    jobQuery: ConnectionIO[Long] =
      sql"""|INSERT INTO job (session_id, job_type, status, created_at)
            |VALUES ($sessionId, ${jobType.toString}, ${jobStatus.toString}, $nowTimestamp)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    jobId                       <- mysqlDriver.use(jobQuery.transact(_))
  } yield JobMod(jobId, sessionId, jobType, jobStatus, nowTimestamp, None)

}
