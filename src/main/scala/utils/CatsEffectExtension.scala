package com.ilovedatajjia
package utils

import cats.effect.IO
import cats.implicits._

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
  implicit class RichArray[A](x: Array[A]) {

    /**
     * Classical traverse on [[IO]] but for [[Array]].
     * @param f
     *   Traverse function giving [[IO]]
     * @tparam B
     *   Wrapped type in the [[IO]]
     * @return
     *   Traversed [[Array]] of [[IO]]
     */
    def traverse[B](f: A => IO[B]): IO[Array[B]] = x.toList.traverse(f).map(_.toArray)

    /**
     * Classical foldLeftM on [[IO]] but for [[Array]].
     * @param f
     *   Fold left function giving [[IO]]
     * @tparam B
     *   Wrapped type in the [[IO]]
     * @return
     *   Folded left [[Array]] of [[IO]]
     */
    def foldLeftM[B](u0: B)(f: (B, A) => IO[B]): IO[B] = x.toList.foldLeftM(u0)(f)

  }

}
