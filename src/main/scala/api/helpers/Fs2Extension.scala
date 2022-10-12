package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import fs2.Stream

/**
 * Extension methods for `Fs2` objects.
 */
object Fs2Extension {

  /**
   * [[fs2.Stream.take]] but do nothing for `nbElements = -1`.
   * @param nbElements
   *   Number of elements taken
   * @return
   *   [[x]] with certain elements taken
   */
  def takeAllOrNotString(x: Stream[IO, String], nbElements: Long): Stream[IO, String] = {
    case -1L => x
    case _   => x.take(nbElements)
  }

}
