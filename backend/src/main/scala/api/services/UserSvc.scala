package com.ilovedatajjia
package api.services

import api.dto.input.LoginFormIDto
import api.dto.output._
import api.helpers.BackendException._
import api.helpers.BackendException.AppException
import api.helpers.DoobieUtils._
import api.helpers.StringUtils._
import api.models._
import cats.effect._
import cats.implicits._
import com.softwaremill.quicklens._
import config.ConfigLoader
import java.sql.Date
import java.sql.Timestamp
import org.postgresql.util.PSQLException

/**
 * Service layer for user.
 */
trait UserSvc {

  /**
   * Convert [[UserMod]] to DTO.
   * @param user
   *   User to display
   * @return
   *   [[UserStatusODto]]
   */
  def toDto(user: UserMod): IO[UserStatusODto] = IO(
    UserStatusODto(user.id,
                   user.email,
                   user.username,
                   user.createdAt.getNanos,
                   user.validatedAt.map(_.getTime),
                   user.updatedAt.getTime,
                   user.activeAt.getTime))

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
  def createUser(email: String, username: String, pwd: String, birthDate: Date)(implicit
      userModDB: UserMod.DB): IO[UserStatusODto] = for {
    pwdSalt <- genString(32)
    pwd     <- s"$pwdSalt$pwd".toSHA3_512 // Hash password logic
    user    <- userModDB(email, username, pwd, pwdSalt, birthDate).attemptT.leftMap {
                 case t: PSQLException => AppException(t.getServerErrorMessage.getDetail)
                 case t                => t
               }.rethrowT
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
  def loginUser(form: LoginFormIDto)(implicit userModDB: UserMod.DB, tokenModDB: TokenMod.DB): IO[TokensODto] = for {
    // Verify login
    potUsers        <- userModDB.select(fr"email = ${form.email}")
    validatedUser   <- potUsers match {
                         case List(user) =>
                           for {
                             isValidCred <- s"${user.pwdSalt}${form.pwd}".eqSHA3_512(user.pwd) // Hash password logic
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
    genExpireAt     <- Clock[IO].realTime.map(x => new Timestamp((x + ConfigLoader.tokenDuration).toMillis))
    genRefreshToken <- genString(100)
    inDBToken       <- tokenModDB.select(fr"user_id = ${validatedUser.id}")
    token           <- inDBToken match {
                         case List(token) =>
                           tokenModDB.update(
                             token
                               .modify(_.accessToken)
                               .setTo(genAccessToken)
                               .modify(_.expireAt)
                               .setTo(genExpireAt)
                               .modify(_.refreshToken)
                               .setTo(genRefreshToken))
                         case _           => tokenModDB(validatedUser.id, genAccessToken, genExpireAt, genRefreshToken)
                       }
  } yield TokensODto(token.accessToken, token.expireAt.getTime, token.refreshToken)

  /**
   * Validate access token.
   * @param accessToken
   *   Access token
   * @return
   *   [[UserMod]]
   */
  def grantAccess(accessToken: String)(implicit userModDB: UserMod.DB, tokenModDB: TokenMod.DB): IO[UserMod] =
    for {
      nowTimestamp <- Clock[IO].realTime.map(_.toMillis)
      potTokens    <- tokenModDB.select(fr"access_token = $accessToken")
      user         <- potTokens match {
                        case List(token) =>
                          if (nowTimestamp < token.expireAt.getTime) userModDB.select(token.userId)
                          else IO.raiseError(AuthException("Expired token provided. Please refresh your token."))
                        case _           =>
                          IO.raiseError(
                            AuthException(
                              "Invalid access token provided. Please refresh your tokens or reconnect your account."))
                      }
      userUpToDate <- userModDB.update(user.modify(_.activeAt).setTo(new Timestamp(nowTimestamp)))
    } yield userUpToDate

  /**
   * Validate refresh token.
   * @param refreshToken
   *   Refresh token
   * @return
   *   New refreshed [[TokenMod]]
   */
  def grantTokens(refreshToken: String)(implicit userModDB: UserMod.DB, tokenModDB: TokenMod.DB): IO[TokensODto] = for {
    // Pre-requisite
    nowTimestamp <- Clock[IO].realTime.map(_.toMillis)

    // Refresh token
    potTokens <- tokenModDB.select(fr"refresh_token = $refreshToken")
    outToken  <- potTokens match {
                   case List(token) =>
                     for {
                       genAccessToken  <- genString(100)
                       genExpireAt      = new Timestamp(nowTimestamp + ConfigLoader.tokenDuration.toMillis)
                       genRefreshToken <- genString(100)
                       out             <- tokenModDB.update(
                                            token
                                              .modify(_.accessToken)
                                              .setTo(genAccessToken)
                                              .modify(_.expireAt)
                                              .setTo(genExpireAt)
                                              .modify(_.refreshToken)
                                              .setTo(genRefreshToken))
                     } yield out
                   case _           =>
                     IO.raiseError(AuthException("Invalid refresh token provided. Please reconnect your account."))
                 }

    // Update user activity
    user      <- userModDB.select(outToken.userId)
    _         <- userModDB.update(user.modify(_.activeAt).setTo(new Timestamp(nowTimestamp)))
  } yield TokensODto(outToken.accessToken, outToken.expireAt.getTime, outToken.refreshToken)

}

/**
 * Auto-DI on import.
 */
object UserSvc { implicit val impl: UserSvc = new UserSvc {} }
