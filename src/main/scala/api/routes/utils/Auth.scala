package com.ilovedatajjia
package api.routes.utils

import api.controllers.SessionCtrl
import api.models.SessionMod
import cats.data._
import cats.effect.IO
import cats.implicits._
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

  /**
   * Validate bearer authorization from a request.
   * @param request
   *   Request with its bearer token to validate
   * @return
   *   [[Right]] with validated token or [[Left]] with error message
   */
  private def authBearerFmtValidator(request: Request[IO]): Either[String, String] = for {
    authorizationHeader <-
      request.headers
        .get[Authorization]
        .toRight("Please verify `Authorization` header and its value are correctly formatted & provided")
    authToken           <- authorizationHeader.credentials match {
                             case Credentials.Token(AuthScheme.Bearer, authToken) =>
                               Right(authToken)
                             case Credentials.Token(authSchemeNotValid, _)        =>
                               Left(s"Expecting `Bearer` authorization prefix but got `$authSchemeNotValid`")
                             case x                                               =>
                               Left(s"Expecting `Token` credentials but got `${x.getClass}` credentials")
                           }
  } yield authToken

  // Define the error if failure == Left (here Forbidden == 403)
  private val onFailure: AuthedRoutes[String, IO] = Kleisli(req => OptionT.liftF(Forbidden(req.context)))

  // Middleware for session auth
  val withAuth: AuthMiddleware[IO, SessionMod] = {
    // Lambda verify session
    val verifySession: Kleisli[IO, String, Either[String, SessionMod]] = Kleisli({ authToken =>
      SessionCtrl
        .verifyAuthorization(authToken)
        .redeem(
          (e: Throwable) => Left(e.toString),
          (session: SessionMod) => Right(session)
        )
    })

    // Lambda verify bearer token & session
    val sessionAuthPolicy: Kleisli[IO, Request[IO], Either[String, SessionMod]] = Kleisli({ request: Request[IO] =>
      // Check valid header `Authorization` & Prepare the `authToken` for session verification
      val validatedAuthToken: Either[String, String] = authBearerFmtValidator(request)

      // Do session verification
      val validatedSession: IO[Either[String, SessionMod]] = validatedAuthToken.flatTraverse(verifySession.run)
      validatedSession // The two `Either` are merged by the `_.flatten` after the `_.traverse`
    })

    // Return
    AuthMiddleware(sessionAuthPolicy, onFailure)
  }

}
