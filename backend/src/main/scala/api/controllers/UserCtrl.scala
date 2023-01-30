package com.ilovedatajjia
package api.controllers

import api.dto.input.CreateUserFormDtoIn
import api.dto.output.UserStatusDtoOut
import api.helpers.AppException
import api.helpers.StringUtils._
import api.services.UserSvc
import cats.effect._
import cats.implicits._
import java.sql.Timestamp
import scala.concurrent.duration._

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
    _              <- IO.raiseUnless(createUserFormDtoIn.email.isValidEmail)(AppException("Email format invalid."))
    _              <- IO.raiseUnless("[a-zA-Z0-9]{2,32}".r.matches(createUserFormDtoIn.username))(
                        AppException("Username must contains 2 to 32 alphanumerical characters."))
    _              <-
      IO.raiseUnless(createUserFormDtoIn.pwd.isValidPwd)(AppException(
        "Password must contains 8 to 32 characters, an uppercase and lowercase letter, a number and a special character."))

    // Validate birth day
    birthTimestamp <-
      IO(Timestamp
        .valueOf(
          s"${createUserFormDtoIn.yearBirth}-${f"${createUserFormDtoIn.monthBirth}%02d"}-${f"${createUserFormDtoIn.dayBirth}%02d"} 00:00:00")
        .getTime).attempt
        .map(_.leftMap(_ =>
          AppException("Invalid birth date. It must respects `yyyy` (Year), `mm` (Month) and `dd` (Day).")))
        .rethrow
    nowTimestamp   <- Clock[IO].realTime.map(_.toMillis)
    _              <- IO.raiseUnless(nowTimestamp - birthTimestamp >= 378683112000L)(
                        AppException("Being at least 12 years old is required."))

    // Create
    dtoOut         <- UserSvc.createUser(createUserFormDtoIn)
  } yield dtoOut

}
