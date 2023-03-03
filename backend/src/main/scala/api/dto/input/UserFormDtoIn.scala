package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for user account creation form.
 * @param email
 *   E-mail unique identifier of the account
 * @param username
 *   Name displayed on the account
 * @param pwd
 *   Password defined
 * @param birthDate
 *   Birth date
 */
case class UserFormDtoIn(email: String, username: String, pwd: String, birthDate: String)

/**
 * [[UserFormDtoIn]] companion.
 */
object UserFormDtoIn {

  // JSON (de)serializers
  implicit val enc: Encoder[UserFormDtoIn] = deriveEncoder
  implicit val dec: Decoder[UserFormDtoIn] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[UserFormDtoIn] = Schema.derived

}
