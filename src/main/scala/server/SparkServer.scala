package com.ilovedatajjia
package server

import cats.effect.IO
import org.apache.spark.sql.SparkSession

/**
 * Spark service.
 */
object SparkServer {

  // Variable to use for Spark API
  var spark: SparkSession = _

  /**
   * Run the Spark single node service.
   */
  def run: IO[Unit] = IO {
    spark = SparkSession
      .builder()
      .appName("")
      .master("local[*]")
      .config("spark.scheduler.mode", "FAIR")
      .getOrCreate()
  }

}
