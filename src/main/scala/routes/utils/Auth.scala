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

  // Runnable retrieve session with authorization token
  private val retrieveSession: Kleisli[IO, String, Either[String, Session]] = Kleisli({ authToken =>
    SessionController
      .verifyAuthorization(authToken)
      .redeem(
        (e: Throwable) => Left(e.toString),
        (session: Session) => Right(session)
      )
  })

  // Authorization verification policy
  private val authPolicy: Kleisli[IO, Request[IO], Either[String, Session]] = Kleisli({ request =>
    // Check valid header `Authorization` & Prepare the `authToken` for session verification
    val validatedAuthToken: Either[String, String] = for {
      authorizationHeader <-
        request.headers.get[Authorization].toRight("Could not find OAuth 2.0 `Authorization` header")
      session             <- authorizationHeader.credentials match {
                               case Credentials.Token(AuthScheme.Bearer, authToken) =>
                                 Right(authToken)
                               case Credentials.Token(authSchema, _)                =>
                                 Left(s"Expecting `Bearer` authorization prefix but got `$authSchema`")
                               case x                                               =>
                                 Left(s"Expecting `Token` credentials but got `${x.getClass}` credentials")
                             }
    } yield session

    // Do session verification
    val validatedSession: IO[Either[String, Either[String, Session]]] = validatedAuthToken.traverse(retrieveSession.run)
    validatedSession.map(_.flatten) // Merge the two `Either`
  })

  // Middleware to actual routes
  private val onFailure: AuthedRoutes[String, IO] =
    Kleisli(req => OptionT.liftF(Forbidden(req.context))) // Define the error if failure == Left (here Forbidden == 403)
  val withAuth: AuthMiddleware[IO, Session] = AuthMiddleware(authPolicy, onFailure)

}
