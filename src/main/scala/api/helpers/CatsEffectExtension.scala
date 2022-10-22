package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import scala.concurrent.duration.FiniteDuration

/**
 * Extension methods for [[cats.effect]] objects.
 */
object CatsEffectExtension {

  /**
   * Rich functions for [[IO]].
   * @param x
   *   An io
   * @tparam A
   *   Wrapped type
   */
  implicit class CatsEffectExtensionRichIO[A](x: IO[A]) {

    /**
     * Apply or not a timeout.
     * @param time
     *   Timeout duration
     * @return
     *   Timeout applied or not
     */
    def timeoutOption(time: Option[FiniteDuration]): IO[A] = time match {
      case None       => x
      case Some(time) => x.timeout(time)
    }

  }

}
