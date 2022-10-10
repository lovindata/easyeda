package com.ilovedatajjia
package api.helpers

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.unsafe.implicits.global.compute
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext

/**
 * [[AsyncFreeSpec]] scala test spec with [[cats.effect.testing]] matchers and runtime.
 */
trait CustomCatsEffectSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  // Global thread pool
  override implicit val executionContext: ExecutionContext = compute

}
