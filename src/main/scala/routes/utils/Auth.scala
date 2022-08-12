package com.ilovedatajjia
package routes.utils

import cats.data._
import cats.effect.IO
import cats.implicits._
import controllers.SessionController
import models.Session
import org.http4s._
import org.http4s.AuthScheme
import org.http4s.Credentials
import org.http4s.Request
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

/**
 * Authorization utils for routes.
 */
object Auth {

  // Authorization verification policy
  private val authPolicy: Kleisli[IO, Request[IO], Either[String, Session]] = Kleisli({ request =>
    // Build session verification if existing & valid authorization
    val verifyIO: Either[String, IO[Session]] = for {
      authorizationHeader <-
        request.headers.get[Authorization].toRight("Could not find OAuth 2.0 `Authorization` header")
      session             <- authorizationHeader.credentials match {
                               case Credentials.Token(AuthScheme.Bearer, authToken) =>
                                 Right(SessionController.verifyAuthorization(authToken))
                               case Credentials.Token(authSchema, _)                =>
                                 Left(s"Expecting `Bearer` authorization prefix but got `$authSchema`")
                               case x                                               =>
                                 Left(s"Expecting `Token` credentials but got `${x.getClass}` credentials")
                             }
    } yield session

    // Prepare session verification (Either[_, IO[Session]] to IO[Either[_, Session]])
    verifyIO.sequence
  })

  // Middleware to actual routes
  private val onFailure: AuthedRoutes[String, IO] =
    Kleisli(req => OptionT.liftF(Forbidden(req.context))) // Define the error if failure == Left (here Forbidden == 403)
  val withAuth: AuthMiddleware[IO, Session] = AuthMiddleware(authPolicy, onFailure)

}
