package com.ilovedatajjia
package api.dto.output

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO out for login token.
 * @param accessToken
 *   Access token
 * @param expireAt
 *   Access token expiration in UTC milliseconds
 * @param refreshToken
 *   Refresh token
 */
case class TokensODto(accessToken: String, expireAt: Long, refreshToken: String)

/**
 * [[TokensODto]] companion.
 */
object TokensODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[TokensODto] = deriveEncoder
  implicit val dec: Decoder[TokensODto] = deriveDecoder
  implicit val sch: Schema[TokensODto]  = Schema.derived

}
