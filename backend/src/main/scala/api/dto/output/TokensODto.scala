package com.ilovedatajjia
package api.dto.output

import api.helpers.CirceUtils._
import api.helpers.TapirUtils._
import io.circe._
import io.circe.generic.semiauto._
import java.sql.Timestamp
import sttp.tapir.Schema

/**
 * DTO out for login token.
 * @param accessToken
 *   Access token
 * @param expireAt
 *   Timestamp with time zone indicating expiration
 * @param refreshToken
 *   Refresh token
 */
case class TokensODto(accessToken: String, expireAt: Timestamp, refreshToken: String)

/**
 * [[TokensODto]] companion.
 */
object TokensODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[TokensODto] = deriveEncoder
  implicit val dec: Decoder[TokensODto] = deriveDecoder
  implicit val sch: Schema[TokensODto]  = Schema.derived

}
