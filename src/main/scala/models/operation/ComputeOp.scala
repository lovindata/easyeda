package com.ilovedatajjia
package models.operation

import cats.effect.IO
import models.job.Job
import org.apache.spark.sql.DataFrame

/**
 * DB representation of an operation.
 * @param id
 *   Spark operation ID
 * @param jobId
 *   Reference a [[Job]]
 * @param opIdx
 *   Operation index for the [[Job]]
 */
abstract class ComputeOp(id: Long, jobId: Long, opIdx: Int) extends SparkOp(id, jobId, opIdx) {

  /**
   * Fit the Spark operation.
   * @param input
   *   The input DataFrame
   * @return
   *   Spark [[DataFrame]] with the operation applied
   */
  def fitOp(input: DataFrame): IO[DataFrame]

}
