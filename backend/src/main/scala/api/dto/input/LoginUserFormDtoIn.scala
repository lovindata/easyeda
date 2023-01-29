package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for user create account form.
 *
 * @param email
 *   E-mail unique identifier of the account
 * @param pwd
 *   Password defined
 */
case class LoginUserFormDtoIn(email: String, pwd: String)

/**
 * [[LoginUserFormDtoIn]] companion.
 */
object LoginUserFormDtoIn {

  // JSON (de)serializers
  implicit val enc: Encoder[LoginUserFormDtoIn] = deriveEncoder
  implicit val dec: Decoder[LoginUserFormDtoIn] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[LoginUserFormDtoIn] = Schema.derived

}
