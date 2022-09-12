package com.ilovedatajjia
package models.job

import cats.effect.IO
import doobie._
import doobie.implicits._
import java.text.SimpleDateFormat
import models.utils.DBDriver.mysqlDriver

/**
 * DB representation of a job parameters.
 */
sealed trait JobParams

/**
 * ADT of [[JobParams]].
 */
object JobParams {

  /**
   * DB representation of a csv job parameters.
   * @param id
   *   Csv parameters ID
   * @param sep
   *   Separator character(s) for the csv
   * @param quote
   *   Quote character(s) for the csv
   * @param escape
   *   Escape character(s) for the csv
   * @param header
   *   If the csv contains a header or not
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   */
  case class CsvParams(id: Long,
                       sep: String,
                       quote: String,
                       escape: String,
                       header: Boolean,
                       inferSchema: Boolean,
                       dateFormat: Option[SimpleDateFormat],
                       timestampFormat: Option[SimpleDateFormat])
      extends JobParams

  /**
   * Additional [[CsvParams]] functions.
   */
  object CsvParams {

    /**
     * Constructor of [[CsvParams]].
     * @param sep
     *   Separator character(s) for the csv
     * @param quote
     *   Quote character(s) for the csv
     * @param escape
     *   Escape character(s) for the csv
     * @param header
     *   If the csv contains a header or not
     * @param inferSchema
     *   If infer the DataFrame schema or not
     * @param dateFormat
     *   Date format to consider when inferring the schema
     * @param timestampFormat
     *   Timestamp format to consider when inferring the schema
     * @return
     *   A new created csv parameters
     */
    def apply(sep: String,
              quote: String,
              escape: String,
              header: Boolean,
              inferSchema: Boolean,
              dateFormat: Option[SimpleDateFormat],
              timestampFormat: Option[SimpleDateFormat]): IO[JobParams] = {

      // Define queries
      val csvParamsTableQuery: ConnectionIO[Long] =
        sql"""|INSERT INTO csv_params (sep, quote, escape, header, infer_schema, date_format, timestamp_format)
              |VALUES ($sep, $quote, $escape, $header, $inferSchema, $dateFormat, $timestampFormat)
              |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

      // Run & Get the auto-incremented ID
      for {
        csvParamsId                            <- mysqlDriver.use(csvParamsTableQuery.transact(_))
        jobParamsTableQuery: ConnectionIO[Long] =
          sql"""|INSERT INTO job_params (paramsType, csv_params_id, json_params_id)
                |VALUES ('csv', $csvParamsId, NULL)
                |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")
        _                                      <- mysqlDriver.use(jobParamsTableQuery.transact(_))
      } yield CsvParams(csvParamsId, sep, quote, escape, header, inferSchema, dateFormat, timestampFormat)

    }

  }

  /**
   * DB representation of a json job parameters.
   * @param id
   *   Json parameters ID
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   */
  case class JsonParams(id: Long,
                        inferSchema: Boolean,
                        dateFormat: Option[SimpleDateFormat],
                        timestampFormat: Option[SimpleDateFormat])
      extends JobParams {}

}
