package com.ilovedatajjia
package api.models

import api.helpers.NormType._
import cats.effect.IO
import config.DBDriver._
import doobie._
import doobie.implicits._
import io.circe.syntax.EncoderOps
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.collection.mutable

/**
 * DB representation of an operation.
 *
 * @param id
 *   Spark operation ID
 * @param jobId
 *   Reference a [[JobMod]]
 * @param opIdx
 *   Operation index
 * @param sparkArg
 *   Operation arguments
 */
case class SparkOpMod(id: Long, jobId: Long, opIdx: Int, sparkArg: SparkArg)

/**
 * Additional functions of [[SparkOpMod]].
 */
object SparkOpMod {

  /**
   * Constructor of [[SparkOpMod]].
   *
   * @param jobId
   *   Reference to [[JobMod]]
   * @param opIdx
   *   Operation idx for a given [[JobMod]]
   * @param sparkArg
   *   Input parameters
   * @return
   *   A new created [[SparkOpMod]]
   */
  def apply(jobId: Long, opIdx: Int, sparkArg: SparkArg): IO[SparkOpMod] = {
    // Prepare the query
    val query: ConnectionIO[Long] =
      sql"""|INSERT INTO spark_op (job_id, op_idx, spark_arg)
            |VALUES ($jobId, $opIdx, ${sparkArg.asJson})
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      id <- postgresDriver.use(query.transact(_))
    } yield SparkOpMod(id, jobId, opIdx, sparkArg)
  }

  /**
   * Retrieve a preview representation of the input DataFrame.
   * @param input
   *   The input DataFrame to retrieve the preview
   * @param nbRows
   *   Sample size number of rows
   * @param nbCols
   *   Sample size number of columns
   * @return
   *   Preview DataFrame schema & values
   */
  def preview(input: DataFrame, nbRows: Int, nbCols: Int): IO[(Array[(String, NormType)], Array[Array[String]])] = IO {
    // Retrieve the sample schema
    val scalaSchema: Array[(String, NormType)] = input.schema.fields.slice(0, nbCols).map {
      case StructField(name, dataType, _, _) => (name, dataType.toNormType)
    }

    // Prepare the Spark DAG for sampling values
    val inputColsToAllString: Array[Column] =
      input.columns.slice(0, nbCols).map(colName => col(colName).cast(StringType).as(colName))
    val inputAllString: DataFrame           = input.select(inputColsToAllString: _*).na.fill("") // To handle "null" values
    val inputColForValues: Column           = array(inputAllString.columns.map(col): _*) as "_$VALUES_"
    val inputValues: DataFrame              = inputAllString.select(
      inputColForValues
    ) // 1 Column where each row "i" is in format "[<_c0_vi>, ..., <_cj_vi>, ..., <_cn_vi>]"

    // Retrieve the sample values
    val rowValues: Array[Row]             = inputValues.head(nbRows)
    val scalaValues: Array[Array[String]] = rowValues
      .map(
        _.getAs[mutable.ArraySeq[String]]("_$VALUES_")
      ) // ArrayType(StringType) == ArraySeq[String]
      .map(_.toArray)

    // Return
    (scalaSchema, scalaValues)
  }

}
