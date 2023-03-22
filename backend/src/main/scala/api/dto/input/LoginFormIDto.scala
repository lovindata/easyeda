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
case class LoginFormIDto(email: String, pwd: String)

/**
 * [[LoginFormIDto]] companion.
 */
object LoginFormIDto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[LoginFormIDto] = deriveEncoder
  implicit val dec: Decoder[LoginFormIDto] = deriveDecoder
  implicit val sch: Schema[LoginFormIDto]  = Schema.derived

}
