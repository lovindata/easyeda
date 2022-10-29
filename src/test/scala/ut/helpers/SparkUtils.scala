package com.ilovedatajjia
package ut.helpers

import api.helpers.CatsEffectExtension._
import cats.data.EitherT
import cats.effect.IO
import cats.effect.implicits._
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
   *   A Spark [[DataFrame]] OR
   *   - [[Exception]] if issue occurred
   */
  def fromResourceDataFrame(path: String, schema: String)(implicit
      spark: SparkSession): EitherT[IO, Exception, DataFrame] =
    IO(Source.fromFile(getClass.getResource(schema).getPath)).attemptE.bracket { x: BufferedSource =>
      IO {
        val strDDL: String = x.mkString
        spark.read
          .schema { if (strDDL.isBlank) StructType(Nil) else StructType.fromDDL(strDDL) }
          .option("multiLine", "true")
          .json(getClass.getResource(path).getPath)
      }.attemptE
    }(x => EitherT.right(IO(x.close())))

}
