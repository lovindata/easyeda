package com.ilovedatajjia
package api.services

import api.dto.input.CreateUserFormDtoIn
import api.dto.input.LoginUserFormDtoIn
import api.dto.output._
import api.helpers.AppException
import api.helpers.StringUtils._
import api.models.TokenMod
import api.models.UserMod
import api.models.UserMod._
import cats.effect._
import doobie.implicits._

/**
 * Service layer for user.
 */
object UserSvc {

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
  } yield UserStatusDtoOut(user.id,
                           user.email,
                           user.username,
                           user.createdAt,
                           user.validatedAt,
                           user.updatedAt,
                           user.activeAt)

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
    potUsers      <- select(fr"email = ${form.email}")
    validatedUser <- potUsers match {
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

    // Generate token
    token         <- TokenMod(validatedUser.id)
  } yield TokenDtoOut(token.accessToken, token.expireAt, token.refreshToken)

}
