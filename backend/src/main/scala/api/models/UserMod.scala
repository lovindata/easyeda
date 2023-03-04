package com.ilovedatajjia
package api.models

import api.helpers.DoobieUtils._
import cats.effect._
import cats.implicits._
import java.sql.Date
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
 *   Hashed password with salt
 * @param pwdSalt
 *   Salt used in argon2 hash
 * @param birthDate
 *   Birth date
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
                   birthDate: Date,
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
   * @param email
   *   Email
   * @param username
   *   Username
   * @param pwd
   *   Password
   * @param pwdSalt
   *   Password salt
   * @param birthDate
   *   Birth date
   * @return
   *   A new created user
   */
  def apply(email: String, username: String, pwd: String, pwdSalt: String, birthDate: Date): IO[UserMod] = for {
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    out          <- insert(
                      UserMod(
                        -1,
                        email,
                        username,
                        pwd,
                        pwdSalt,
                        birthDate,
                        none,
                        nowTimestamp,
                        none,
                        nowTimestamp,
                        nowTimestamp
                      ))
  } yield out

}
