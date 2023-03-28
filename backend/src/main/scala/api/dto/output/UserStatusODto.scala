package com.ilovedatajjia
package api.dto.output

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * User status.
 * @param id
 *   User id
 * @param email
 *   E-mail unique identifier of the account
 * @param username
 *   Name displayed on the account
 * @param createdAt
 *   Account created at
 * @param validatedAt
 *   Account email validated at in UTC milliseconds
 * @param updatedAt
 *   Account updated at in UTC milliseconds
 * @param activeAt
 *   Account latest activity at in UTC milliseconds
 */
case class UserStatusODto(id: Long,
                          email: String,
                          username: String,
                          createdAt: Long,
                          validatedAt: Option[Long],
                          updatedAt: Long,
                          activeAt: Long)

/**
 * [[UserStatusODto]] companion.
 */
object UserStatusODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[UserStatusODto] = deriveEncoder
  implicit val dec: Decoder[UserStatusODto] = deriveDecoder
  implicit val sch: Schema[UserStatusODto]  = Schema.derived

}
