package com.ilovedatajjia
package api.helpers

import cats.data.EitherT
import cats.effect.IO
import cats.effect.implicits._
import cats.implicits._
import scala.concurrent.TimeoutException
import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

/**
 * Extension methods for [[cats.effect]] objects.
 */
object CatsEffectExtension {

  /**
   * Implicit to make [[EitherT]] covariant on [[Left]] and [[Right]].
   * @param x
   *   Source invariant [[EitherT]]
   * @tparam E
   *   [[Left]] source type
   * @tparam A
   *   [[Right]] source type
   * @tparam E2
   *   [[Left]] closest type subtype of [[E]]
   * @tparam A2
   *   [[Right]] closest type subtype of [[A]]
   * @return
   *   Covariant [[EitherT]]
   */
  implicit def eitherTCovariant[E, A, E2 >: E, A2 >: A](x: EitherT[IO, E, A]): EitherT[IO, E2, A2] =
    x.widen[A2].leftWiden[E2]

  /**
   * Rich functions for [[IO]].
   * @param x
   *   An io
   * @tparam A
   *   Wrapped type
   */
  implicit class CatsEffectExtensionRichIO[A](x: IO[A]) {

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
     * Apply or not a [[IO.timeout]], it manages [[TimeoutException]] as left in the case of timeout. (Method implicitly
     * covariant in left and right)
     * @param time
     *   Timeout duration with [[None]] means infinite
     * @return
     *   Timeout applied or not
     */
    def timeoutOptionTo[E2 >: E, A2 >: A](time: Option[FiniteDuration], fallbackTo: E2): EitherT[IO, E2, A2] =
      time match {
        case None        => x.widen[A2].leftWiden[E2]
        case Some(sTime) => x.widen[A2].leftWiden[E2].timeoutTo(sTime, EitherT.left[A2](IO(fallbackTo)))
      }

  }

}
