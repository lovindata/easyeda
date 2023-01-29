package com.ilovedatajjia
package api.routes

import api.controllers.UserCtrl
import api.dto.input.CreateUserFormDtoIn
import api.dto.input.LoginUserFormDtoIn
import api.dto.output.TokenDtoOut
import api.dto.output.UserStatusDtoOut
import api.helpers.AppException
import api.helpers.AppException._
import api.services.UserSvc
import cats.effect.IO
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for users management.
 */
object UserRts {

  // Create user
  private val createEpt: PublicEndpoint[CreateUserFormDtoIn, AppException, UserStatusDtoOut, Any] = endpoint
    .summary("Create user account")
    .post
    .in("user" / "create")
    .in(jsonBody[CreateUserFormDtoIn])
    .out(jsonBody[UserStatusDtoOut])
    .errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[AppException]))
  private val createRts: HttpRoutes[IO]                                                           =
    Http4sServerInterpreter[IO]().toRoutes(createEpt.serverLogic(UserCtrl.createUser(_).toErrHandled))

  // Login user
  private val loginEpt: PublicEndpoint[LoginUserFormDtoIn, AppException, TokenDtoOut, Any] = endpoint
    .summary("Login user account")
    .post
    .in("user" / "login")
    .in(jsonBody[LoginUserFormDtoIn])
    .out(jsonBody[TokenDtoOut])
    .errorOut(statusCode(StatusCode.BadRequest).and(jsonBody[AppException]))
  private val loginRts: HttpRoutes[IO]                                                     =
    Http4sServerInterpreter[IO]().toRoutes(loginEpt.serverLogic(UserSvc.loginUser(_).toErrHandled))

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  def docEpt: List[AnyEndpoint] = List(createEpt, loginEpt)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  def appRts: HttpRoutes[IO] = createRts <+> loginRts

}
