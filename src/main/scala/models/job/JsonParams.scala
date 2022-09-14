package com.ilovedatajjia
package models.job

import cats.effect.IO
import doobie._
import doobie.implicits._
import java.text.SimpleDateFormat
import models.utils.DBDriver.mysqlDriver

/**
 * DB representation of a json file parameters ([[FileParams]]).
 * @param id
 *   Json parameters ID
 * @param jobId
 *   Reference a [[Job]]
 * @param inferSchema
 *   If infer the DataFrame schema or not
 * @param dateFormat
 *   Date format to consider when inferring the schema
 * @param timestampFormat
 *   Timestamp format to consider when inferring the schema
 */
case class JsonParams(id: Long,
                      jobId: Long,
                      inferSchema: Boolean,
                      dateFormat: Option[SimpleDateFormat],
                      timestampFormat: Option[SimpleDateFormat])
    extends FileParams

/**
 * Additional [[JsonParams]] functions.
 */
object JsonParams {

  /**
   * Constructor of [[JsonParams]].
   * @param jobId
   *   Reference a [[Job]]
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   * @return
   *   A new created csv parameters
   */
  def apply(jobId: Long,
            inferSchema: Boolean,
            dateFormat: Option[SimpleDateFormat],
            timestampFormat: Option[SimpleDateFormat]): IO[FileParams] = {

    // Define query
    val jsonParamsTableQuery: ConnectionIO[Long] =
      sql"""|INSERT INTO json_params (job_id, sep, quote, escape, header, infer_schema, date_format, timestamp_format)
            |VALUES ($jobId, $inferSchema, $dateFormat, $timestampFormat)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      jsonParamsId <- mysqlDriver.use(jsonParamsTableQuery.transact(_))
    } yield JsonParams(jsonParamsId, jobId, inferSchema, dateFormat, timestampFormat)

  }

}
