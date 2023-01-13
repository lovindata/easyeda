package com.ilovedatajjia
package api.dto.output

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * User status.
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
case class UserStatusDtoOut(email: String,
                            username: String,
                            createdAt: String,
                            validatedAt: Option[String],
                            updatedAt: String,
                            activeAt: String)

/**
 * [[UserStatusDtoOut]] companion.
 */
object UserStatusDtoOut {

  // JSON (de)serializers
  implicit val enc: Encoder[UserStatusDtoOut] = deriveEncoder
  implicit val dec: Decoder[UserStatusDtoOut] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[UserStatusDtoOut] = Schema.derived

}
