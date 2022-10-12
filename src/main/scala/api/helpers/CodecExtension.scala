package com.ilovedatajjia
package api.helpers

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Extension methods for encoding objects.
 */
object CodecExtension {

  /**
   * Rich functions applicable on [[String]].
   * @param x
   *   Supposed in UTF-8
   */
  implicit class CodecRichString(x: String) {

    /**
     * Hash [[x]] to SHA-1 hexadecimal string representation.
     * @return
     *   SHA-1 hexadecimal of [[x]]
     */
    def toSha1Hex: String = {

      // Specify the encoding of `sessionAuthToken` as UTF_8 before the SHA-1
      val encodedSessionAuthToken: Array[Byte] = MessageDigest
        .getInstance("SHA-1")
        .digest(x.getBytes(StandardCharsets.UTF_8))

      // Binary to hex string
      encodedSessionAuthToken.map(x => String.format("%02x", Byte.box(x))).mkString

    }

    /**
     * Verify if the SHA-1 hashed [[x]] is equal to the value.
     * @param inputSha1Hex
     *   Supposed an hexadecimal string
     * @return
     *   `true` if equals `false` otherwise
     */
    def inSha1HexEquals(inputSha1Hex: String): Boolean = x.toSha1Hex == inputSha1Hex

  }

}
