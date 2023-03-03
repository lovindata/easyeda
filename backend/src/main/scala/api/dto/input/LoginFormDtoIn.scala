package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for user login form.
 * @param email
 *   E-mail unique identifier of the account
 * @param pwd
 *   Password defined
 */
case class LoginFormDtoIn(email: String, pwd: String)

/**
 * [[LoginFormDtoIn]] companion.
 */
object LoginFormDtoIn {

  // JSON (de)serializers
  implicit val enc: Encoder[LoginFormDtoIn] = deriveEncoder
  implicit val dec: Decoder[LoginFormDtoIn] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[LoginFormDtoIn] = Schema.derived

}
