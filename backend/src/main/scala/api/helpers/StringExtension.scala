package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import cats.implicits._
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Utils for [[String]].
 */
object StringExtension {

  /**
   * Rich for [[String]].
   * @param x
   *   Applied on
   */
  implicit class StringExtensionRichString(x: String) {

    /**
     * Validate email format. (RFC 5322 official format)
     * @return
     *   Valid email or not
     */
    def isValidEmail: Boolean =
      "^((?:[A-Za-z0-9!#$%&'*+\\-/=?^_`{|}~]|(?<=^|\\.)\"|\"(?=$|\\.|@)|(?<=\".*)[ .](?=.*\")|(?<!\\.)\\.){1,64})(@)([A-Za-z0-9.\\-]*[A-Za-z0-9]\\.[A-Za-z0-9]{2,})$".r
        .matches(x)

    /**
     * Validate password format requirements.
     *   - 8 and 32 characters
     *   - One uppercase letter
     *   - One lowercase letter
     *   - One number character
     *   - One special character
     * @return
     *   Valid password or not
     */
    def isValidPwd: Boolean =
      "^.{8,32}$".r.matches(x) && "^[A-Z]+$".r.matches(x) && "^[a-z]+$".r.matches(x) && "^[0-9]+$".r.matches(
        x) && "^[^A-Za-z0-9]+$".r.matches(x)

    /**
     * Convert [[x]] to hashed with SHA3-512.
     * @return
     *   Hashed of [[x]]
     * @note
     *   Fast hash algorithm will be used for hashing password. But, the right way is to use a slower and more computing
     *   intensive one for example the latest state-of-the art [[https://github.com/phxql/argon2-jvm Argon2]].
     */
    def toSHA3_512: IO[String] = for {
      encoder <- IO(MessageDigest.getInstance("SHA3-512"))
      encoded <- IO(encoder.digest(x.getBytes(StandardCharsets.UTF_8)))
      out     <- encoded.toList.parTraverse(x => IO(String.format("%02x", Byte.box(x)))).map(_.mkString)
    } yield out

    /**
     * Verify [[x]] against a hash.
     * @param hash
     *   Hash
     * @return
     *   [[x]] hashed equal to the provided hash
     */
    def verifySHA3_512(hash: String): IO[Boolean] = x.toSHA3_512.map(_ == hash)

  }

}
