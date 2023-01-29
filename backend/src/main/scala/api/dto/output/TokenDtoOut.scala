package com.ilovedatajjia
package api.dto.output

import api.helpers.TapirUtils._
import api.helpers.CirceUtils._
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
case class TokenDtoOut(accessToken: String, expireAt: Timestamp, refreshToken: String)

/**
 * [[TokenDtoOut]] companion.
 */
object TokenDtoOut {

  // JSON (de)serializers
  implicit val enc: Encoder[TokenDtoOut] = deriveEncoder
  implicit val dec: Decoder[TokenDtoOut] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[TokenDtoOut] = Schema.derived

}
