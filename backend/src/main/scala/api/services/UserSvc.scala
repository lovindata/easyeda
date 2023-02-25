package com.ilovedatajjia
package api.services

import api.dto.input.LoginUserFormDtoIn
import api.dto.output._
import api.helpers.AppException
import api.helpers.StringUtils._
import api.models.TokenMod
import api.models.UserMod
import cats.effect._
import com.softwaremill.quicklens._
import config.ConfigLoader
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.postgres.circe.json.implicits._
import doobie.postgres.implicits._
import java.sql.Date
import java.sql.Timestamp

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
   * @param email
   *   Validated email
   * @param username
   *   Validated pseudo
   * @param pwd
   *   Validated password
   * @param birthDate
   *   Validated birth date
   * @return
   *   User status
   */
  def createUser(email: String, username: String, pwd: String, birthDate: Date): IO[UserStatusDtoOut] = for {
    pwdSalt <- genString(32)
    pwd     <- s"$pwdSalt$pwd".toSHA3_512
    user    <- UserMod(email, username, pwd, pwdSalt, birthDate)
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
    genAccessToken  <- genString(100)
    genExpireAt     <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis + (ConfigLoader.tokenDuration.toLong * 1000)))
    genRefreshToken <- genString(100)
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
   * @param accessToken
   *   Access token
   * @return
   *   [[UserMod]]
   */
  def grantAccess(accessToken: String): IO[UserMod] = for {
    nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
    potTokens    <- TokenMod.select(fr"access_token = $accessToken")
    user         <- potTokens match {
                      case List(token) =>
                        if (nowTimestamp < token.expireAt.getTime) UserMod.select(token.userId)
                        else IO.raiseError(AppException("Expired token provided. Please refresh your token."))
                      case _           => IO.raiseError(AppException("Invalid token provided. Please reconnect your account."))
                    }
    userUpToDate <- UserMod.update(user.modify(_.activeAt).setTo(new Timestamp(nowTimestamp)))
  } yield userUpToDate

  /**
   * Validate refresh token.
   * @param refreshToken
   *   Refresh token
   * @return
   *   New refreshed [[TokenMod]]
   */
  def grantToken(refreshToken: String): IO[TokenDtoOut] = for {
    // Pre-requisite
    nowTimestamp <- Clock[IO].realTime.map(_.toMillis)

    // Refresh token
    potTokens <- TokenMod.select(fr"refresh_token = $refreshToken")
    outToken  <- potTokens match {
                   case List(token) =>
                     for {
                       genAccessToken  <- genString(100)
                       genExpireAt      = new Timestamp(nowTimestamp + (ConfigLoader.tokenDuration.toLong * 1000))
                       genRefreshToken <- genString(100)
                       out             <- TokenMod.update(
                                            token
                                              .modify(_.accessToken)
                                              .setTo(genAccessToken)
                                              .modify(_.expireAt)
                                              .setTo(genExpireAt)
                                              .modify(_.refreshToken)
                                              .setTo(genRefreshToken))
                     } yield out
                   case _           =>
                     IO.raiseError(AppException("Invalid refresh token provided. Please reconnect your account."))
                 }

    // Update user activity
    user      <- UserMod.select(outToken.userId)
    _         <- UserMod.update(user.modify(_.activeAt).setTo(new Timestamp(nowTimestamp)))
  } yield TokenDtoOut(outToken.accessToken, outToken.expireAt, outToken.refreshToken)

}
