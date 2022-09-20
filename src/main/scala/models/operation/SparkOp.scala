package com.ilovedatajjia
package models.operation

import cats.effect.IO
import doobie._
import doobie.implicits._
import io.circe.Json
import io.circe.syntax.EncoderOps
import models.job.Job
import models.utils.DBDriver.mysqlDriver
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.collection.mutable

/**
 * DB representation of an operation.
 * @param id
 *   Spark operation ID
 * @param jobId
 *   Reference a [[Job]]
 * @param opIdx
 *   Operation index
 * @param sparkArg
 *   Operation arguments
 */
case class SparkOp(id: Long, jobId: Long, opIdx: Int, sparkArg: SparkArg)

/**
 * Additional functions of [[SparkOp]].
 */
object SparkOp {

  /**
   * Constructor of [[SparkOp]].
   * @param jobId
   *   Reference to [[Job]]
   * @param opIdx
   *   Operation idx for a given [[Job]]
   * @param sparkArg
   *   Input parameters
   * @return
   *   A new created [[SparkOp]]
   */
  def apply(jobId: Long, opIdx: Int, sparkArg: SparkArg): IO[SparkOp] = {
    // Prepare the query
    val query: ConnectionIO[Long] =
      sql"""|INSERT INTO spark_op (job_id, op_idx, spark_arg)
            |VALUES ($jobId, $opIdx, ${sparkArg.asJson})
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      id <- mysqlDriver.use(query.transact(_))
    } yield SparkOp(id, jobId, opIdx, sparkArg)
  }

  /**
   * Retrieve a preview representation of the input DataFrame.
   * @param input
   *   The input DataFrame to retrieve the preview
   * @param sampleSize
   *   Sample size default at 20 rows
   * @return
   *   [[Json]] array of array of string, representing `sampleSize` rows of the input DataFrame
   */
  def preview(input: DataFrame, sampleSize: Int = 20): IO[Json] = IO {
    // Prepare the Spark DAG for sampling
    val inputColsToAllString: Array[Column] =
      input.columns.map(colName => col(colName).cast(StringType) as colName)
    val inputAllString: DataFrame           =
      input.select(inputColsToAllString: _*).na.fill("") // To handle "null" values
    val inputColForValues: Column =
      array(inputAllString.columns.map(col): _*) as "_$VALUES_"
    val inputValues: DataFrame    =
      input.select(
        inputColForValues
      ) // 1 Column where each row "i" is in format "[<_c0_vi>, ..., <_cj_vi>, ..., <_cn_vi>]"

    // Retrieve the sample as Json
    val rowValues: Array[Row]             = inputValues.head(sampleSize)
    val scalaValues: Array[Array[String]] = rowValues
      .map(
        _.getAs[mutable.ArraySeq[String]]("_$VALUES_")
      ) // ArrayType(StringType) == ArraySeq[String]
      .map(_.toArray)

    // Return
    scalaValues.asJson
  }

}
