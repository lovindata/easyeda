package com.ilovedatajjia
package api.services

import api.dto.input.CreateUserFormDtoIn
import api.dto.input.LoginUserFormDtoIn
import api.dto.output._
import api.helpers.AppException
import api.helpers.StringUtils._
import api.models.TokenMod
import api.models.UserMod
import cats.effect._
import config.ConfigLoader
import com.softwaremill.quicklens._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres.circe.json.implicits._
import doobie.postgres.implicits._
import java.sql.Timestamp // Needed import for Meta mapping

/**
 * Service layer for user.
 */
object UserSvc {

  /**
   * Convert [[UserMod]] to DTO.
   * @param user
   *   User to display
   * @return
   *   [[UserStatusDtoOut]]
   */
  def toDto(user: UserMod): IO[UserStatusDtoOut] = IO(
    UserStatusDtoOut(user.id,
                     user.email,
                     user.username,
                     user.createdAt,
                     user.validatedAt,
                     user.updatedAt,
                     user.activeAt))

  /**
   * Create the user.
   * @param createUserFormDtoIn
   *   User creation form
   * @return
   *   User status
   */
  def createUser(createUserFormDtoIn: CreateUserFormDtoIn): IO[UserStatusDtoOut] = for {
    pwdSalt <- genString(32)
    pwd     <- s"$pwdSalt${createUserFormDtoIn.pwd}".toSHA3_512
    user    <- UserMod(createUserFormDtoIn, pwd, pwdSalt)
    userDto <- this.toDto(user)
  } yield userDto

  /**
   * Verify provided login.
   * @param form
   *   Login to validate
   * @return
   *   Tokens OR
   *   - [[AppException]] if incorrect login
   */
  def loginUser(form: LoginUserFormDtoIn): IO[TokenDtoOut] = for {
    // Verify login
    potUsers        <- UserMod.select(fr"email = ${form.email}")
    validatedUser   <- potUsers match {
                         case List(user) =>
                           for {
                             isValidCred <- s"${user.pwdSalt}${form.pwd}".eqSHA3_512(user.pwd)
                             _           <- IO.raiseUnless(isValidCred)(
                                              AppException(
                                                "Invalid username or password. Please check your credentials and try again."))
                           } yield user
                         case _          =>
                           IO.raiseError(
                             AppException("Invalid username or password. Please check your credentials and try again."))
                       }

    // Check if existing valid token then provide a valid one
    genAccessToken  <- genString(64)
    genExpireAt     <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis + (ConfigLoader.tokenDuration.toLong * 1000)))
    genRefreshToken <- genString(64)
    inDBToken       <- TokenMod.select(fr"user_id = ${validatedUser.id}")
    token           <- inDBToken match {
                         case List(token) =>
                           TokenMod.update(
                             token
                               .modify(_.accessToken)
                               .setTo(genAccessToken)
                               .modify(_.expireAt)
                               .setTo(genExpireAt)
                               .modify(_.refreshToken)
                               .setTo(genRefreshToken))
                         case _           => TokenMod(validatedUser.id, genAccessToken, genExpireAt, genRefreshToken)
                       }
  } yield TokenDtoOut(token.accessToken, token.expireAt, token.refreshToken)

  /**
   * Validate access token.
   * @param token
   *   Access token
   * @return
   *   [[UserMod]]
   */
  def grantAccess(token: String): IO[UserMod] = for {
    potTokens <- TokenMod.select(fr"access_token = $token") // TODO fix bug here
    user      <- potTokens match {
                   case List(token) =>
                     for {
                       nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
                       user         <- if (nowTimestamp < token.expireAt.getTime) UserMod.select(token.userId)
                                       else IO.raiseError(AppException("Expired token provided. Please refresh your token."))
                     } yield user
                   case _           => IO.raiseError(AppException("Invalid token provided. Please reconnect your account."))
                 }
  } yield user

}
