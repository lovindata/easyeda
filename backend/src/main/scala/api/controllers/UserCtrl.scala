package com.ilovedatajjia
package api.controllers

import api.dto.input.CreateUserFormDtoIn
import api.dto.output.UserStatusDtoOut
import api.helpers.AppException
import api.helpers.StringUtils._
import api.services.UserSvc
import cats.effect._
import java.sql.Date

/**
 * Controller layer for user.
 */
object UserCtrl {

  /**
   * Validate form and create the user.
   * @param createUserFormDtoIn
   *   Form to validate
   * @return
   *   User status OR
   *   - [[AppException]] if a form issue
   */
  def createUser(createUserFormDtoIn: CreateUserFormDtoIn): IO[UserStatusDtoOut] = for {
    // Validate email, username and password
    _         <- IO.raiseUnless(createUserFormDtoIn.email.isValidEmail)(AppException("Email format invalid."))
    _         <- IO.raiseUnless("[a-zA-Z0-9]{2,32}".r.matches(createUserFormDtoIn.username))(
                   AppException("Username must contains 2 to 32 alphanumerical characters."))
    _         <-
      IO.raiseUnless(createUserFormDtoIn.pwd.isValidPwd)(AppException(
        "Password must contains 8 to 32 characters, an uppercase and lowercase letter, a number and a special character."))

    // Validate birth day
    birthDate <- IO(Date.valueOf(createUserFormDtoIn.birthDate)).attempt.map {
                   case Left(_)  => throw AppException("Invalid birth date format.")
                   case Right(x) => x
                 }
    nowMillis <- Clock[IO].realTime.map(_.toMillis)
    _         <- IO.raiseUnless(nowMillis - birthDate.getTime >= 378683112000L)(
                   AppException("Being at least 12 years old is required."))

    // Create
    dtoOut    <- UserSvc.createUser(createUserFormDtoIn.email, createUserFormDtoIn.username, createUserFormDtoIn.pwd, birthDate)
  } yield dtoOut

}
