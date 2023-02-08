package com.ilovedatajjia
package api.dto.input

import api.helpers.TapirUtils._
import api.helpers.CirceUtils._
import io.circe._
import io.circe.generic.semiauto._
import java.sql.Date
import sttp.tapir.Schema

/**
 * DTO for user create account form.
 * @param email
 *   E-mail unique identifier of the account
 * @param username
 *   Name displayed on the account
 * @param pwd
 *   Password defined
 * @param birthDate
 *   Birth date
 */
case class CreateUserFormDtoIn(email: String, username: String, pwd: String, birthDate: Date)

/**
 * [[CreateUserFormDtoIn]] companion.
 */
object CreateUserFormDtoIn {

  // JSON (de)serializers
  implicit val enc: Encoder[CreateUserFormDtoIn] = deriveEncoder
  implicit val dec: Decoder[CreateUserFormDtoIn] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[CreateUserFormDtoIn] = Schema.derived

}
