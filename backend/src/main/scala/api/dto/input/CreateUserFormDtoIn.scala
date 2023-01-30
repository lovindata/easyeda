package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for user create account form.
 * @param email
 *   E-mail unique identifier of the account
 * @param username
 *   Name displayed on the account
 * @param pwd
 *   Password defined
 * @param dayBirth
 *   Day of birth
 * @param monthBirth
 *   Month of birth
 * @param yearBirth
 *   Year of birth
 */
case class CreateUserFormDtoIn(email: String,
                               username: String,
                               pwd: String,
                               yearBirth: Short,
                               monthBirth: Short,
                               dayBirth: Short)

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
