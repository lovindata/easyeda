package com.ilovedatajjia
package models.operation

import cats.effect.IO
import cats.implicits._
import doobie._
import doobie.implicits._
import models.job.Job
import models.utils.DBDriver.mysqlDriver
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import utils.NormType.NormType

/**
 * DB representation of a custom schema operation.
 * @param id
 *   Json read operation ID
 * @param jobId
 *   Reference a [[Job]]
 * @param opIdx
 *   Operation index for the [[Job]]
 * @param schema
 *   Natural index of the column and its type
 */
case class CustomSchOp(id: Long, jobId: Long, opIdx: Int, schema: Array[(Int, NormType)])
    extends ComputeOp(id, jobId, opIdx) {

  /**
   * Apply the Spark operation.
   * @param input
   *   The input DataFrame
   * @return
   *   Spark [[DataFrame]] with the operation applied
   */
  override def fitOp(input: DataFrame): IO[DataFrame] = IO {
    val inputColNames: Array[String] = input.columns
    val inputOutCols: Array[Column]  = schema.map { case (natIdx: Long, targetType: NormType) =>
      val colName: String = inputColNames(natIdx)
      input(colName).cast(targetType.toDataType).as(colName)
    }
    input.select(inputOutCols: _*)
  }

}

/**
 * Additional [[CustomSchOp]] functions.
 */
object CustomSchOp {

  /**
   * Constructor of [[CustomSchOp]].
   * @param jobId
   *   Reference a [[Job]]
   * @param opIdx
   *   Operation index for the [[Job]]
   * @param schema
   *   Array of tuple (NatIdx, NormType)
   * @return
   *   A new created json operation
   */
  def apply(jobId: Long, opIdx: Int, schema: Array[(Int, NormType)]): IO[CustomSchOp] = {

    // Define query
    val query: ConnectionIO[Long] =
      sql"""|INSERT INTO custom_sch_c_op (job_id, op_idx)
            |VALUES ($jobId, $opIdx)
            |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

    // Run & Get the auto-incremented ID
    for {
      id <- mysqlDriver.use(query.transact(_))
      _  <- schema.toList.traverse { case (natIdx: Int, normType: NormType) => CustomCol(id, natIdx, normType) }
    } yield CustomSchOp(id, jobId, opIdx, schema)

  }

  /**
   * DB representation of a column.
   * @param id
   *   Custom column ID
   * @param customSchId
   *   Custom schema ID
   * @param natIdx
   *   Natural idx
   * @param normType
   *   Normalized type
   */
  case class CustomCol(id: Long, customSchId: Long, natIdx: Int, normType: NormType)

  /**
   * Companion of object of [[CustomCol]].
   */
  object CustomCol {

    /**
     * Constructor of [[CustomCol]].
     * @param natIdx
     *   Natural idx
     * @param normType
     *   Normalized type
     * @return
     *   Custom column
     */
    def apply(customSchId: Long, natIdx: Int, normType: NormType): IO[CustomCol] = {

      // Define query
      val query: ConnectionIO[Long] =
        sql"""|INSERT INTO custom_col_p_op (custom_sch_id, nat_idx, norm_type)
              |VALUES ($customSchId, $natIdx, $normType)
              |""".stripMargin.update.withUniqueGeneratedKeys[Long]("id")

      // Run & Get the auto-incremented ID
      for {
        id <- mysqlDriver.use(query.transact(_))
      } yield CustomCol(id, customSchId, natIdx, normType)

    }

  }

}
