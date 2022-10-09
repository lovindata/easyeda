package com.ilovedatajjia
package api.helpers

import io.circe.Json
import io.circe.parser.parse

/**
 * Extension rich functions for `Circe`.
 */
object CirceExtension {

  /**
   * Rich functions for [[String]].
   * @param x
   *   Applied on
   */
  implicit class CirceExtensionRichString(x: String) {

    /**
     * Convert string to [[Json]].
     * @return
     *   Json representation of the string or throw `ParsingFailure`
     */
    def toJson: Json = parse(x) match {
      case Left(error)    => throw error
      case Right(jsonRes) => jsonRes
    }

  }

}
