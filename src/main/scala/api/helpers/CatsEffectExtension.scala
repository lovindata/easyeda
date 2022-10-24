package com.ilovedatajjia
package api.helpers

import cats.data.EitherT
import cats.effect.IO
import cats.effect.implicits._
import cats.implicits._
import scala.concurrent.TimeoutException
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
     * Apply or not a [[IO.timeout]]. It raises [[TimeoutException]] in the case of timeout.
     * @param time
     *   Timeout duration
     * @return
     *   Timeout applied or not
     */
    def timeoutOption(time: Option[FiniteDuration]): IO[A] = time match {
      case None       => x
      case Some(time) => x.timeout(time)
    }

    /**
     * Manage only [[Exception]] and let pass the [[Throwable]].
     * @return
     *   [[EitherT]] for [[Exception]]
     */
    def attemptE: EitherT[IO, Exception, A] = x.attemptT.leftMap {
      case exception: Exception => exception
      case throwable            => throw throwable
    }

  }

  /**
   * Rich functions for [[EitherT]].
   * @param x
   *   An [[EitherT]] of [[IO]]
   * @tparam E
   *   Wrapped exception type
   * @tparam A
   *   Wrapped output type
   */
  implicit class CatsEffectExtensionRichEitherT[E, A](x: EitherT[IO, E, A]) {

    /**
     * Apply or not a [[IO.timeout]]. It manages [[TimeoutException]] as left in the case of timeout.
     * @param time
     *   Timeout duration with [[None]] means infinite
     * @return
     *   Timeout applied or not
     */
    def timeoutOptionTo(time: Option[FiniteDuration], fallbackTo: E): EitherT[IO, E, A] = time match {
      case None       => x
      case Some(time) => x.timeoutTo(time, EitherT.left(IO(fallbackTo)))
    }

  }

}
