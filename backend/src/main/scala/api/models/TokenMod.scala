package com.ilovedatajjia
package api.models

import api.helpers.StringUtils._
import cats.effect._
import java.sql.Timestamp

/**
 * DB representation of user tokens.
 * @param id
 *   Token id
 * @param idUser
 *   User id
 * @param accessToken
 *   Access token
 * @param expireAt
 *   Timestamp with time zone indicating expiration
 * @param refreshToken
 *   Refresh token
 */
case class TokenMod(id: Long, idUser: Long, accessToken: String, expireAt: Timestamp, refreshToken: String)

/**
 * Additional [[TokenMod]] functions.
 */
object TokenMod extends GenericMod[TokenMod] {

  /**
   * Constructor of [[TokenMod]].
   * @return
   *   A new created token
   */
  def apply(idUser: Long): IO[TokenMod] = for {
    nowTimestamp <- Clock[IO].realTime.map(x => new Timestamp(x.toMillis))
    token        <- genString(64)
    refreshToken <- genString(64)
    out          <- insert(
                      TokenMod(
                        -1,
                        idUser,
                        token,
                        nowTimestamp,
                        refreshToken
                      ))
  } yield out

}
