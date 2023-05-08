package com.ilovedatajjia
package api.controllers

import api.dto.input.UserFormIDto
import api.dto.output.UserStatusODto
import api.helpers.BackendException.AppException
import api.helpers.StringUtils._
import api.services.UserSvc
import cats.effect._
import cats.effect.IO
import java.sql.Date

/**
 * Controller layer for user.
 */
trait UserCtrl {

  /**
   * Validate form and create the user.
   * @param createUserFormDtoIn
   *   Form to validate
   * @return
   *   User status OR
   *   - [[AppException]] if a form issue
   */
  def createUser(createUserFormDtoIn: UserFormIDto)(implicit userSvc: UserSvc): IO[UserStatusODto] = for {
    // Validate email, username and password
    _ <- createUserFormDtoIn.email.isValidEmail
    _ <- createUserFormDtoIn.pwd.isValidPwd
    _ <- createUserFormDtoIn.username.isValidName

    // Validate birth day
    birthDate <- IO(Date.valueOf(createUserFormDtoIn.birthDate)).attempt.map {
                   case Left(_)  => throw AppException("Invalid birth date format.")
                   case Right(x) => x
                 }
    nowMillis <- Clock[IO].realTime.map(_.toMillis)
    _         <- IO.raiseUnless(nowMillis - birthDate.getTime >= 378683112000L)( // 12 years == 378683112000L
                   AppException("Being at least 12 years old is required."))

    // Validate terms
    _         <- IO.raiseUnless(createUserFormDtoIn.isTermsAccepted)(
                   AppException("Terms must be read and accepted for creating an account."))

    // Create
    dtoOut    <-
      userSvc.createUser(createUserFormDtoIn.email, createUserFormDtoIn.username, createUserFormDtoIn.pwd, birthDate)
  } yield dtoOut

}

/**
 * Auto-DI on import.
 */
object UserCtrl { implicit val impl: UserCtrl = new UserCtrl {} }
