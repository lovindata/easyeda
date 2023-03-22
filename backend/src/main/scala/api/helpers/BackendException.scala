package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import sttp.tapir.Schema
import sttp.tapir.generic.{Configuration => TapirConfiguration}

/**
 * DTO & Backend exception.
 */
sealed trait BackendException extends Exception {

  // Mandatory field(s)
  val message: String

}

/**
 * ADT of [[BackendException]].
 */
object BackendException {

  // JSON & SwaggerUI
  implicit val conf: Configuration            = Configuration.default.withDiscriminator("kind")
  implicit val enc: Encoder[BackendException] = deriveConfiguredEncoder
  implicit val dec: Decoder[BackendException] = deriveConfiguredDecoder
  implicit val confSch: TapirConfiguration    = TapirConfiguration.default.withDiscriminator("kind")
  implicit val sch: Schema[BackendException]  = Schema.derived

  /**
   * Handled application logic exception.
   * @param message
   *   Exception message
   */
  case class AppException(message: String) extends BackendException

  /**
   * Handled authentication logic exception.
   * @param message
   *   Exception message
   */
  case class AuthException(message: String) extends BackendException

  /**
   * Rich for [[IO]].
   */
  implicit class BackendExceptionRichIO[A](x: IO[A]) {

    /**
     * All exceptions from [[IO]] managed to [[Either]].
     * @return
     *   [[A]] if success output OR
     *   - [[BackendException]] if internal voluntary raised
     *   - All others if internal not voluntary raised
     */
    def toErrHandled: IO[Either[BackendException, A]] = x.redeem(
      {
        case x: BackendException              => Left(x)
        case notVoluntaryThrowable: Throwable =>
          Left(
            AppException(
              s"Unhandled error occurred, it will be managed in next releases (${notVoluntaryThrowable.toString})."))
      },
      Right(_)
    )

  }

}
