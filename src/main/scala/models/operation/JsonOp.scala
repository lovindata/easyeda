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
 * @param opIdx
 *   Operation index for the [[Job]]
 * @param inferSchema
 *   If infer the DataFrame schema or not
 * @param dateFormat
 *   Date format to consider when inferring the schema
 * @param timestampFormat
 *   Timestamp format to consider when inferring the schema
 */
case class JsonOp(id: Long,
                  jobId: Long,
                  opIdx: Int,
                  inferSchema: Boolean,
                  dateFormat: Option[SimpleDateFormat],
                  timestampFormat: Option[SimpleDateFormat])
    extends ReadOp(id, jobId, opIdx) {

  /**
   * Fit the Spark operation.
   * @param input
   *   The input DataFrame in string representation OR actual Spark DataFrame
   * @return
   *   Spark [[DataFrame]] with the operation applied
   */
  override def fitOp(input: String): IO[DataFrame] = IO {

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
    val inputDS: Dataset[String] = spark.createDataset(input.split('\n').toList)
    spark.read.options(readOptions).csv(inputDS)

  }

}

/**
 * Additional [[JsonOp]] functions.
 */
object JsonOp {

  /**
   * Constructor of [[JsonOp]].
   *
   * @param jobId
   *   Reference a [[Job]]
   * @param opIdx
   *   Operation index for the [[Job]]
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
            opIdx: Int,
            inferSchema: Boolean,
            dateFormat: Option[SimpleDateFormat],
            timestampFormat: Option[SimpleDateFormat]): IO[JsonOp] = {

    // Define query
    val query: ConnectionIO[Long] =
      sql"""|INSERT INTO json_r_op (job_id, opIdx, infer_schema, date_format, timestamp_format)
            |VALUES ($jobId, $opIdx, $inferSchema, $dateFormat, $timestampFormat)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      id <- mysqlDriver.use(query.transact(_))
    } yield JsonOp(id, jobId, opIdx, inferSchema, dateFormat, timestampFormat)

  }

}
