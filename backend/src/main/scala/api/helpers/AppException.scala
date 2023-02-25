package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * Handled application logic exception and JSON serializable.
 * @param message
 *   Exception message
 */
case class AppException(message: String) extends Exception(message)

/**
 * [[AppException]] companion.
 */
object AppException {

  // JSON (de)serializer
  implicit val enc: Encoder[AppException] = deriveEncoder
  implicit val dec: Decoder[AppException] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[AppException] = Schema.derived

  /**
   * Rich for [[IO]].
   * @param x
   *   Applied on
   * @tparam A
   *   Wrapped type
   */
  implicit class AppExceptionRichIO[A](x: IO[A]) {

    /**
     * All exceptions from [[IO]] managed to [[Either]].
     * @return
     *   [[A]] if success output OR
     *   - [[AppException]] if internal voluntary raised
     *   - [[AppException]] if internal not voluntary raised
     */
    def toErrHandled: IO[Either[AppException, A]] = x.redeem(
      {
        case x: AppException                  => Left(x)
        case notVoluntaryThrowable: Throwable =>
          Left(
            AppException(
              s"Unhandled error occurred, it will be managed in next releases (${notVoluntaryThrowable.toString})."))
      },
      Right(_)
    )

  }

}
