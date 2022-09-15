package com.ilovedatajjia
package models.operation

import cats.effect.IO
import io.circe.Json
import io.circe.syntax.EncoderOps
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.collection.mutable

/**
 * DB representation of an operation.
 */
trait SparkOperation {

  /**
   * Apply the Spark operation.
   * @return
   *   Spark [[DataFrame]] with the operation applied
   */
  def apply: IO[DataFrame]

}

/**
 * Additional functions of [[SparkOperation]].
 */
object SparkOperation {

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
