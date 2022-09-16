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
 * DB representation of a json read operation.
 * @param id
 *   Json read operation ID
 * @param jobId
 *   Reference a [[Job]]
 * @param inferSchema
 *   If infer the DataFrame schema or not
 * @param dateFormat
 *   Date format to consider when inferring the schema
 * @param timestampFormat
 *   Timestamp format to consider when inferring the schema
 */
case class ReadJsonOperation(id: Long,
                             jobId: Long,
                             inferSchema: Boolean,
                             dateFormat: Option[SimpleDateFormat],
                             timestampFormat: Option[SimpleDateFormat])
    extends SparkOperation {

  /**
   * Apply the Spark operation.
   * @param input
   *   The input DataFrame in string representation OR actual Spark DataFrame
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
    val defaultOptions: Map[String, String]  = Map("mode" -> "FAILFAST", "multiLine" -> "true", "encoding" -> "UTF-8")
    val parsedOptions: Map[String, String]   = Map("primitivesAsString" -> (!inferSchema).toString)
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
 * Additional [[ReadJsonOperation]] functions.
 */
object ReadJsonOperation {

  /**
   * Constructor of [[ReadJsonOperation]].
   * @param jobId
   *   Reference a [[Job]]
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   * @return
   *   A new created json operation
   */
  def apply(jobId: Long,
            inferSchema: Boolean,
            dateFormat: Option[SimpleDateFormat],
            timestampFormat: Option[SimpleDateFormat]): IO[ReadJsonOperation] = {

    // Define query
    val jsonParamsTableQuery: ConnectionIO[Long] =
      sql"""|INSERT INTO json_params (job_id, sep, quote, escape, header, infer_schema, date_format, timestamp_format)
            |VALUES ($jobId, $inferSchema, $dateFormat, $timestampFormat)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      jsonParamsId <- mysqlDriver.use(jsonParamsTableQuery.transact(_))
    } yield ReadJsonOperation(jsonParamsId, jobId, inferSchema, dateFormat, timestampFormat)

  }

}
