package com.ilovedatajjia
package api.models

import api.dto.input.CreateUserFormDtoIn
import cats.effect._
import cats.implicits._
import java.sql.Timestamp

/**
 * DB representation of a user.
 * @param id
 *   User id
 * @param email
 *   User email
 * @param username
 *   Pseudo
 * @param pwd
 *   Argon2 hashed password with salt and pepper
 * @param pwdSalt
 *   Salt used in argon2 hash
 * @param dayBirth
 *   Day of birth
 * @param monthBirth
 *   Month of birth
 * @param yearBirth
 *   Year of birth
 * @param img
 *   Image bytes
 * @param createdAt
 *   User created at
 * @param validatedAt
 *   User validated at
 * @param updatedAt
 *   User updated at
 * @param activeAt
 *   User active at
 */
case class UserMod(id: Long,
                   email: String,
                   username: String,
                   pwd: String,
                   pwdSalt: String,
                   dayBirth: Short,
                   monthBirth: Short,
                   yearBirth: Short,
                   img: Option[Array[Byte]],
                   createdAt: Timestamp,
                   validatedAt: Option[Timestamp],
                   updatedAt: Timestamp,
                   activeAt: Timestamp)

/**
 * Additional [[UserMod]] functions.
 */
object UserMod extends GenericMod[UserMod] {

  /**
   * Constructor of [[UserMod]].
   * @param createUserFormDtoIn
   *   User creation form
   * @param pwd
   *   Password
   * @param pwdSalt
   *   Password salt
   * @return
   *   A new created user
   */
  def apply(createUserFormDtoIn: CreateUserFormDtoIn, pwd: String, pwdSalt: String): IO[UserMod] = for {
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    out          <- insert(
                      UserMod(
                        -1,
                        createUserFormDtoIn.email,
                        createUserFormDtoIn.username,
                        pwd,
                        pwdSalt,
                        createUserFormDtoIn.dayBirth,
                        createUserFormDtoIn.monthBirth,
                        createUserFormDtoIn.yearBirth,
                        none,
                        nowTimestamp,
                        none,
                        nowTimestamp,
                        nowTimestamp
                      ))
  } yield out

}
