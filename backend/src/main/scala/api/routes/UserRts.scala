package com.ilovedatajjia
package api.routes

import api.controllers.UserCtrl
import api.dto.input._
import api.dto.output._
import api.helpers.AppException
import api.helpers.AppException._
import api.models.UserMod
import api.services.UserSvc
import cats.effect.IO
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for users management.
 */
object UserRts extends GenericRts {

  // Create user
  private val createEpt: PublicEndpoint[UserFormDtoIn, AppException, UserStatusDtoOut, Any] = errHandledEpt
    .summary("create user account")
    .post
    .in("user" / "create")
    .in(jsonBody[UserFormDtoIn])
    .out(jsonBody[UserStatusDtoOut])
  private val createRts: HttpRoutes[IO]                                                     =
    Http4sServerInterpreter[IO]().toRoutes(createEpt.serverLogic(UserCtrl.createUser(_).toErrHandled))

  // Login user
  private val loginEpt: PublicEndpoint[LoginFormDtoIn, AppException, TokenDtoOut, Any] = errHandledEpt
    .summary("login user account")
    .post
    .in("user" / "login")
    .in(jsonBody[LoginFormDtoIn])
    .out(jsonBody[TokenDtoOut])
  private val loginRts: HttpRoutes[IO]                                                 =
    Http4sServerInterpreter[IO]().toRoutes(loginEpt.serverLogic(UserSvc.loginUser(_).toErrHandled))

  // Refresh user token
  private val refreshEpt: PublicEndpoint[String, AppException, TokenDtoOut, Any] = errHandledEpt
    .summary("refresh user tokens")
    .post
    .in(auth.bearer[String]())
    .in("user" / "refresh")
    .out(jsonBody[TokenDtoOut])
  private val refreshRts: HttpRoutes[IO]                                         =
    Http4sServerInterpreter[IO]().toRoutes(refreshEpt.serverLogic(UserSvc.grantToken(_).toErrHandled))

  // Retrieve user
  private val getEpt: PartialServerEndpoint[String, UserMod, Unit, AppException, UserStatusDtoOut, Any, IO] = authEpt
    .summary("retrieve user account info")
    .get
    .in("user" / "retrieve")
    .out(jsonBody[UserStatusDtoOut])
  private val getRts: HttpRoutes[IO]                                                                        =
    Http4sServerInterpreter[IO]().toRoutes(getEpt.serverLogic(user => _ => UserSvc.toDto(user).toErrHandled))

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(createEpt, loginEpt, refreshEpt) ++ List(getEpt).map(_.endpoint)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = createRts <+> loginRts <+> refreshRts <+> getRts

}
