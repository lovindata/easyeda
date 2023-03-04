package com.ilovedatajjia
package api.models

import api.helpers.DoobieUtils._
import cats.effect._
import java.sql.Timestamp

/**
 * DB representation of user tokens.
 * @param id
 *   Token id
 * @param userId
 *   User id
 * @param accessToken
 *   Access token
 * @param expireAt
 *   Timestamp with time zone indicating expiration
 * @param refreshToken
 *   Refresh token
 */
case class TokenMod(id: Long, userId: Long, accessToken: String, expireAt: Timestamp, refreshToken: String)

/**
 * Additional [[TokenMod]] functions.
 */
object TokenMod extends GenericMod[TokenMod] {

  /**
   * Constructor of [[TokenMod]].
   * @param userId
   *   Token for this [[UserMod]] id
   * @param accessToken
   *   Access token
   * @param expireAt
   *   Expire at
   * @param refreshToken
   *   Refresh token
   * @return
   *   A new created token
   */
  def apply(userId: Long, accessToken: String, expireAt: Timestamp, refreshToken: String): IO[TokenMod] = insert(
    TokenMod(
      -1,
      userId,
      accessToken,
      expireAt,
      refreshToken
    ))

}
