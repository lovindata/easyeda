package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import cats.implicits._
import scala.concurrent.duration.FiniteDuration
import scala.reflect.ClassTag // Necessary for Array[B]

/**
 * Extension functions for `Cats Effect`.
 */
object CatsEffectExtension {

  /**
   * Rich functions for [[Array]].
   * @param x
   *   An array
   * @tparam A
   *   Wrapped type
   */
  implicit class CatsEffectExtensionRichArray[A](x: Array[A]) {

    /**
     * Classical traverse on [[IO]] but for [[Array]].
     * @param f
     *   Traverse function giving [[IO]]
     * @tparam B
     *   Wrapped type in the [[IO]]
     * @return
     *   Traversed [[Array]] of [[IO]]
     */
    def traverse[B: ClassTag](f: A => IO[B]): IO[Array[B]] = x.toList.traverse(f).map(_.toArray)

    /**
     * Classical foldLeftM on [[IO]] but for [[Array]].
     * @param f
     *   Fold left function giving [[IO]]
     * @tparam B
     *   Wrapped type in the [[IO]]
     * @return
     *   Folded left [[Array]] of [[IO]]
     */
    def foldLeftM[B: ClassTag](u0: B)(f: (B, A) => IO[B]): IO[B] = x.toList.foldLeftM(u0)(f)

  }

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
