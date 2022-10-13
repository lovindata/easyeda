package com.ilovedatajjia
package ut.helpers

import cats.effect.IO
import org.apache.spark.sql._
import org.apache.spark.sql.types.StructType
import scala.io.BufferedSource
import scala.io.Source

/**
 * Static function for building [[org.apache.spark.sql]] objects.
 */
object SparkUtils {

  /**
   * Read JSON [[DataFrame]] representation.
   * @param path
   *   JSON resource path
   * @param schema
   *   The [[DataFrame]] schema resource path
   * @param spark
   *   implicit [[SparkSession]]
   * @return
   *   A Spark [[DataFrame]]
   */
  def fromResourceDataFrame(path: String, schema: String)(implicit spark: SparkSession): IO[DataFrame] =
    IO(Source.fromFile(getClass.getResource(schema).getPath)).bracket { x: BufferedSource =>
      IO(
        spark.read
          .schema(StructType.fromDDL(x.mkString))
          .option("multiLine", "true")
          .json(getClass.getResource(path).getPath)
      )
    }(x => IO(x.close()))

}
