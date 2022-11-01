package com.ilovedatajjia
package api.controllers

import api.dto.output.SessionStatusDtoOut
import api.helpers.AppLayerException
import api.helpers.AppLayerException._
import api.models.SessionMod
import api.models.SessionStateEnum._
import api.services.SessionSvc
import cats.data._
import cats.effect.IO
import cats.implicits._
import org.http4s._
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware

/**
 * Controller for sessions logic.
 */
object SessionCtrl {

  /**
   * Validate bearer authorization from a request.
   * @param request
   *   Request with its bearer token to validate
   * @return
   *   [[Right]] with validated token or [[Left]] with error message
   */
  private def authBearerFmtValidator(request: Request[IO]): Either[AppLayerException, String] = for {
    authorizationHeader <-
      request.headers
        .get[Authorization]
        .toRight(
          ControllerLayerException(
            msgServer = "Please verify `Authorization` header and its value are correctly formatted & provided",
            statusCodeServer = Status.BadRequest))
    authToken           <- authorizationHeader.credentials match {
                             case Credentials.Token(AuthScheme.Bearer, authToken) => Right(authToken)
                             case Credentials.Token(authSchemeNotValid, _)        =>
                               Left(
                                 ControllerLayerException(
                                   msgServer = s"Expecting `Bearer` authorization prefix but got `$authSchemeNotValid`",
                                   statusCodeServer = Status.BadRequest))
                             case x                                               =>
                               Left(
                                 ControllerLayerException(s"Expecting `Token` credentials but got `${x.getClass}` credentials",
                                                          Status.BadRequest))
                           }
  } yield authToken

  /**
   * Middleware for session authentication.
   * @return
   *   Middleware with session auth defined
   */
  def withAuth: AuthMiddleware[IO, SessionMod] = {
    // Lambda verify bearer token & session
    val sessionAuthPolicy: Kleisli[IO, Request[IO], Either[AppLayerException, SessionMod]] =
      Kleisli({ request: Request[IO] =>
        // Check valid header `Authorization` & Prepare the `authToken` for session verification
        val validatedAuthToken: Either[AppLayerException, String] = authBearerFmtValidator(request)

        // Do session verification & Return
        val validatedSession: IO[Either[AppLayerException, SessionMod]] =
          validatedAuthToken.flatTraverse(x =>
            SessionSvc
              .verifyAuthorization(x)
              .value) // The two `Either` are merged by the `_.flatten` after the `_.traverse`
        validatedSession
      })

    // Define the error if failure
    val onFailure: AuthedRoutes[AppLayerException, IO] = Kleisli(req => OptionT.liftF { req.context.toResponseIO })

    // Return
    AuthMiddleware(sessionAuthPolicy, onFailure)
  }

  /**
   * List sessions.
   * @param state
   *   Filtering sessions according a certain state
   * @return
   *   Listing of sessions OR
   *   - [[ControllerLayerException]] if unknown provided state
   */
  def listSessions(state: String): EitherT[IO, AppLayerException, List[SessionStatusDtoOut]] = for {
    // Validate the parameter
    filterState <-
      EitherT(IO(state match {
        case "ALL"             => Right(None)
        case "ACTIVE_ONLY"     => Right(Some(Active))
        case "TERMINATED_ONLY" => Right(Some(Terminated))
        case _                 =>
          Left(
            ControllerLayerException(
              msgServer = s"""Unknown filtering state `$state` not in {"ALL", "ACTIVE_ONLY", "TERMINATED_ONLY"}""",
              statusCodeServer = Status.BadRequest))
      }))

    // Starting listing
    sessions    <- EitherT.right(SessionSvc.listSessions(filterState))
  } yield sessions

}
