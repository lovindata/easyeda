package com.ilovedatajjia
package routes

import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import com.ilovedatajjia.models.Session
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.server._
import org.http4s.syntax.header._
import routes.utils._

/**
 * Utils for routes.
 */
package object utils {

  /**
   * Http4s default entity encoder using an implicit circe encoder.
   * @return
   *   Implicit entity encoder
   */
  // implicit def entityEncoder[A]: EntityEncoder[IO, A] = jsonEncoderOf[IO, A]

  // Authentication
  /*
  val authUserHeaders: Kleisli[IO, Request[IO], Either[String, Session]] = Kleisli({ request =>
    val message = for {
      header  <- request.headers
                   .get[Authorization]
                   .toRight("Couldn't find an Authorization header")
      token   <- crypto
                   .validateSignedToken(header.value)
                   .toRight("Invalid token")
      message <- Either
                   .catchOnly[NumberFormatException](token.toLong)
                   .leftMap(_.toString)
    } yield message
    message.traverse(retrieveUser.run)
  })
   */

}
