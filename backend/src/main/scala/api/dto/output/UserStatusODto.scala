package com.ilovedatajjia
package api.dto.output

import api.helpers.CirceUtils._ // Needed import
import api.helpers.TapirUtils._ // Needed import
import io.circe._
import io.circe.generic.semiauto._
import java.sql.Timestamp
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
 *   Account email validated at
 * @param updatedAt
 *   Account updated at
 * @param activeAt
 *   Account latest activity at
 */
case class UserStatusODto(id: Long,
                          email: String,
                          username: String,
                          createdAt: Timestamp,
                          validatedAt: Option[Timestamp],
                          updatedAt: Timestamp,
                          activeAt: Timestamp)

/**
 * [[UserStatusODto]] companion.
 */
object UserStatusODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[UserStatusODto] = deriveEncoder
  implicit val dec: Decoder[UserStatusODto] = deriveDecoder
  implicit val sch: Schema[UserStatusODto]  = Schema.derived

}
