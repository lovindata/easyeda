package com.ilovedatajjia
package api.models

import api.helpers.StringUtils._
import cats.effect._
import config.ConfigLoader
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
   * @return
   *   A new created token
   */
  def apply(userId: Long): IO[TokenMod] = for {
    genAccessToken  <- genString(64)
    genExpireAt     <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis + (ConfigLoader.tokenDuration.toLong * 1000)))
    genRefreshToken <- genString(64)
    out             <- insert(
                         TokenMod(
                           -1,
                           userId,
                           genAccessToken,
                           genExpireAt,
                           genRefreshToken
                         ))
  } yield out

}
