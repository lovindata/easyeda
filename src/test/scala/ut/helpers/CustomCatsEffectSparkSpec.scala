package com.ilovedatajjia
package ut.helpers

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.unsafe.implicits.global.compute
import org.apache.spark.sql.SparkSession
import org.scalatest.BeforeAndAfterAll
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext

/**
 * [[AsyncFreeSpec]] scala test spec with [[cats.effect.testing]] matchers & runtime and [[SparkSession]] (will start
 * only if at least one of your test needs it).
 */
trait CustomCatsEffectSparkSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers with BeforeAndAfterAll {

  // Global thread pool
  override implicit val executionContext: ExecutionContext = compute

  // Lazy spark session (will be initialized only when necessary)
  implicit lazy val spark: SparkSession = SparkSession
    .builder()
    .appName("EasyEDATest")
    .master("local[*]")
    .config("spark.ui.port", "4040")
    .config("spark.scheduler.mode", "FAIR")
    .config("spark.scheduler.allocation.file", getClass.getResource("/ut/helpers/fairscheduler.xml").getPath)
    .config("spark.scheduler.pool", "fairPoolTest") // Pool name defined in the XML file
    .getOrCreate()

}
