package com.ilovedatajjia
package api.helpers

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession

/**
 * Static function for building [[org.apache.spark.sql]] objects.
 */
object SparkUtils {

  def fromResourceDataFrame(path: String, schema: String)(implicit spark: SparkSession): DataFrame = ??? // TODO

}
