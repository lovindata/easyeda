package com.ilovedatajjia
package models.operation

import cats.effect.IO
import doobie._
import doobie.implicits._
import java.text.SimpleDateFormat
import models.job.Job
import models.utils.DBDriver.mysqlDriver
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Dataset
import server.SparkServer._

/**
 * DB representation of a csv read operation.
 * @param id
 *   Csv read operation ID
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
case class ReadCsvOperation(id: Long,
                            jobId: Long,
                            sep: String,
                            quote: String,
                            escape: String,
                            header: Boolean,
                            inferSchema: Boolean,
                            dateFormat: Option[SimpleDateFormat],
                            timestampFormat: Option[SimpleDateFormat])
    extends SparkOperation {

  /**
   * Apply the Spark operation.
   * @param input
   *   The input DataFrame
   * @return
   *   Spark [[DataFrame]] with the operation applied
   */
  override def applyOperation(input: Either[String, DataFrame]): IO[DataFrame] = IO {

    // Retrieve the string representation
    val actInput: String = input match {
      case Left(input) => input
      case Right(_)    => throw new IllegalArgumentException("Please make sure `input` is in string representation")
    }

    // Build read options
    val defaultOptions: Map[String, String]  = Map("mode" -> "FAILFAST", "multiLine" -> "true")
    val parsedOptions: Map[String, String]   = Map("sep" -> sep,
                                                 "quote"       -> quote,
                                                 "escape"      -> escape,
                                                 "header"      -> header.toString,
                                                 "inferSchema" -> inferSchema.toString)
    val optionalOptions: Map[String, String] =
      dateFormat.map(x => Map("dateFormat" -> x.toString)).getOrElse(Map.empty[String, String]) ++
        timestampFormat
          .map(x => Map("timestampFormat" -> x.toString))
          .getOrElse(Map.empty[String, String])
    val readOptions: Map[String, String]     = defaultOptions ++ parsedOptions ++ optionalOptions

    // Build & Return the Spark DataFrame
    import spark.implicits._
    val inputDS: Dataset[String] = spark.createDataset(actInput.split('\n').toList)
    spark.read.options(readOptions).csv(inputDS)

  }

}

/**
 * Additional [[ReadCsvOperation]] functions.
 */
object ReadCsvOperation {

  /**
   * Constructor of [[ReadCsvOperation]].
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
   *   A new created csv operation
   */
  def apply(jobId: Long,
            sep: String,
            quote: String,
            escape: String,
            header: Boolean,
            inferSchema: Boolean,
            dateFormat: Option[SimpleDateFormat],
            timestampFormat: Option[SimpleDateFormat]): IO[ReadCsvOperation] = {

    // Define query
    val csvParamsTableQuery: ConnectionIO[Long] =
      sql"""|INSERT INTO csv_params (job_id, sep, quote, escape, header, infer_schema, date_format, timestamp_format)
            |VALUES ($jobId, $sep, $quote, $escape, $header, $inferSchema, $dateFormat, $timestampFormat)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      csvParamsId <- mysqlDriver.use(csvParamsTableQuery.transact(_))
    } yield ReadCsvOperation(csvParamsId, jobId, sep, quote, escape, header, inferSchema, dateFormat, timestampFormat)

  }

}
