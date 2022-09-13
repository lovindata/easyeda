package com.ilovedatajjia
package models.job

import cats.effect.IO
import doobie._
import doobie.implicits._
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import java.text.SimpleDateFormat
import models.utils.DBDriver.mysqlDriver

/**
 * DB representation of a csv file parameters ([[FileParams]]).
 *
 * @param id
 *   Csv parameters ID
 * @param jobId
 *   Reference a [[Job]]
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
                     jobId: Long,
                     sep: String,
                     quote: String,
                     escape: String,
                     header: Boolean,
                     inferSchema: Boolean,
                     dateFormat: Option[SimpleDateFormat],
                     timestampFormat: Option[SimpleDateFormat])
    extends FileParams

/**
 * Additional [[CsvParams]] functions.
 */
object CsvParams {

  // Decoder
  implicit val csvParamsDecoder: Decoder[CsvParams] = deriveConfiguredDecoder

  /**
   * Constructor of [[CsvParams]].
   * @param jobId
   *   Reference a [[Job]]
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
  def apply(jobId: Long,
            sep: String,
            quote: String,
            escape: String,
            header: Boolean,
            inferSchema: Boolean,
            dateFormat: Option[SimpleDateFormat],
            timestampFormat: Option[SimpleDateFormat]): IO[CsvParams] = {

    // Define query
    val csvParamsTableQuery: ConnectionIO[Long] =
      sql"""|INSERT INTO csv_params (job_id, sep, quote, escape, header, infer_schema, date_format, timestamp_format)
            |VALUES ($jobId, $sep, $quote, $escape, $header, $inferSchema, $dateFormat, $timestampFormat)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      csvParamsId <- mysqlDriver.use(csvParamsTableQuery.transact(_))
    } yield CsvParams(csvParamsId, jobId, sep, quote, escape, header, inferSchema, dateFormat, timestampFormat)

  }

}
