package com.ilovedatajjia
package controllers

import cats.effect.Clock
import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.implicits._
import java.nio.charset.StandardCharsets
import java.util.Base64
import routes.session.entity.SessionAuthEntity
import routes.session.entity.SessionStatusEntity
import com.ilovedatajjia.models.session.Session

/**
 * Controller for sessions logic.
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
   *   Session UUID & Authorization token
   */
  def createSession: IO[SessionAuthEntity] = for {
    // Generate authorization token
    authToken      <-
      (UUIDGen[IO].randomUUID, Clock[IO].monotonic) // `Clock[IO].monotonic` == now unix timestamp in nanoseconds
        .mapN((someUUID, nowUnixTimestamp) => s"$someUUID:$nowUnixTimestamp")
        .map(intermediateToken => Base64.getEncoder.encodeToString(intermediateToken.getBytes(StandardCharsets.UTF_8)))

    // Create & Persist the new session
    createdSession <- Session(authToken)
    _              <- createdSession.startCronJobInactivityCheck() // Start also the inactivity checker
  } yield SessionAuthEntity(createdSession.id, authToken)

  /**
   * Terminate the provided session.
   * @param validatedSession
   *   A validated session
   * @return
   *   Session updated status
   */
  def terminateSession(validatedSession: Session): IO[SessionStatusEntity] = for {
    _              <- Session.terminateWithId(validatedSession.id)
    updatedSession <- Session.getWithId(validatedSession.id)
  } yield SessionStatusEntity(updatedSession.id,
                              updatedSession.createdAt.toString,
                              updatedSession.updatedAt.toString,
                              updatedSession.terminatedAt.map(_.toString))

  /**
   * List all non terminated sessions.
   * @param validatedSession
   *   A validated session
   * @return
   *   Listing of all non terminated sessions
   */
  def listSessions(validatedSession: Session): IO[Array[SessionStatusEntity]] = for {
    sessions      <- Session.listActiveSessions
    sessionsStatus = sessions.map(session =>
                       SessionStatusEntity(session.id,
                                           session.createdAt.toString,
                                           session.updatedAt.toString,
                                           session.terminatedAt.map(_.toString)))
  } yield sessionsStatus

}
