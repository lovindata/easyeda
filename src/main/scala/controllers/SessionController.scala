package com.ilovedatajjia
package controllers

import cats.effect.Clock
import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.implicits.catsSyntaxTuple2Semigroupal
import java.nio.charset.StandardCharsets
import java.util.Base64
import models.Session

/**
 * Controller for session logic.
 */
object SessionController {

  /**
   * Verify if existing [[Session]] & Report its activity. (Exception thrown if no session can be retrieved)
   * @param authTokenToVerify
   *   The brut authorization token
   * @return
   *   The identified session
   */
  def verifyAuthorization(authTokenToVerify: String): IO[Session] = for {
    _       <- Session.refreshWithAuthToken(authTokenToVerify)
    session <- Session.getWithAuthToken(authTokenToVerify)
  } yield session

  /**
   * Create a new [[Session]].
   * @return
   *   Authorization token & Created session
   */
  def createSession: IO[String] = for {
    // Generate UUID & Authorization token
    sessionUUID    <- UUIDGen[IO].randomUUID
    authToken      <-
      (UUIDGen[IO].randomUUID, Clock[IO].monotonic) // `Clock[IO].monotonic` == now unix timestamp in nanoseconds
        .mapN((someUUID, nowUnixTimestamp) => s"$someUUID:$nowUnixTimestamp")
        .map(intermediateToken => Base64.getEncoder.encodeToString(intermediateToken.getBytes(StandardCharsets.UTF_8)))

    // Create & Persist the new session
    createdSession <- Session(sessionUUID, authToken)
    _              <- createdSession.persist
    _              <- createdSession.startCronJobInactivityCheck() // Start also the inactivity checker
  } yield authToken

  /**
   * Terminate the provided session. (Exception thrown if issue occurred)
   * @param validatedSession
   *   A validated session (DO NOT USE THIS FOR NON VALIDATED)
   * @return
   *   Updated version of `validatedSession`
   */
  def terminateSession(validatedSession: Session): IO[Session] = for {
    _              <- Session.terminateWithId(validatedSession.id)
    updatedSession <- Session.getWithId(validatedSession.id)
  } yield updatedSession

  /**
   * List all non terminated sessions.
   * @param validatedSession
   *   A validated session
   * @return
   *   Listing of all non terminated sessions
   */
  def listSessions(validatedSession: Session): IO[Array[Session]] = for {
    sessions <- Session.listActiveSessions
  } yield sessions

}
