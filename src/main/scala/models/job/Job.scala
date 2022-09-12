package com.ilovedatajjia
package models.job

import java.sql.Timestamp
import models.job.Job._

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
 * @param jobParamsId
 *   Job parameters ID
 * @param jobResultId
 *   Job result ID
 * @param createdAt
 *   Job creation timestamp
 * @param terminatedAt
 *   Job termination timestamp
 */
case class Job(id: Long,
               sessionId: Long,
               jobType: JobType,
               status: JobType,
               jobParamsId: Long,
               jobResultId: Long,
               createdAt: Timestamp,
               terminatedAt: Timestamp)

/**
 * Additional [[Job]] functions.
 */
object Job {

  // Enumeration of possible jobs
  sealed trait JobType
  case object Preview extends JobType
  case object Analyze extends JobType

  // Enumeration of possible job status
  sealed trait JobStatus
  case object Running    extends JobStatus
  case object Terminated extends JobStatus

}
