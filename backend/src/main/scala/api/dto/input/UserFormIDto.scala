package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for user account creation form.
 * @param email
 *   E-mail unique identifier of the account
 * @param pwd
 *   Password defined
 * @param username
 *   Name displayed on the account
 * @param birthDate
 *   Birth date in "yyyy-MM-dd" format
 * @param isTermsAccepted
 *   Contract terms accepted or not
 */
case class UserFormIDto(email: String, pwd: String, username: String, birthDate: String, isTermsAccepted: Boolean)

/**
 * [[UserFormIDto]] companion.
 */
object UserFormIDto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[UserFormIDto] = deriveEncoder
  implicit val dec: Decoder[UserFormIDto] = deriveDecoder
  implicit val sch: Schema[UserFormIDto]  = Schema.derived

}
