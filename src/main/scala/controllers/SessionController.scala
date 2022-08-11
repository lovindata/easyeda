package com.ilovedatajjia
package controllers

import cats.effect.Clock
import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.implicits.catsSyntaxTuple2Semigroupal
import controllers.utils.codec._
import java.nio.charset.StandardCharsets
import java.util.Base64
import models.Session

/**
 * Controller for session logic.
 */
object SessionController {

  /**
   * Create a new [[Session]].
   * @return
   *   Authentication token & Created session
   */
  def createSession: IO[String] = for {

    // Generate UUID & Authentication token
    sessionUUID             <- UUIDGen[IO].randomUUID
    authToken               <-
      (Clock[IO].monotonic, UUIDGen[IO].randomUUID) // `Clock[IO].monotonic` == now unix timestamp in nanoseconds
        .mapN((nowUnixTimestamp, someUUID) => s"$nowUnixTimestamp $someUUID")
        .map(intermediateToken => Base64.getEncoder.encodeToString(intermediateToken.getBytes(StandardCharsets.UTF_8)))

    // Encode to SHA1 the authentication token
    encodedAuthToken: String = authToken.toSha1Hex

    // Create & Persist the new session
    createdSession <- Session(sessionUUID, encodedAuthToken)
    _              <- createdSession.persistSession
    _              <- createdSession.startCronJobInactivityCheck() // Start also the inactivity checker

  } yield authToken

}
