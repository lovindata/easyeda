package com.ilovedatajjia
package api.controllers

import api.models.SessionMod
import api.routes.entities.SessionAuthEnt
import api.routes.entities.SessionStatusEnt
import cats.effect.Clock
import cats.effect.IO
import cats.effect.std.UUIDGen
import cats.implicits._
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Controller for sessions logic.
 */
object SessionCtrl {

  /**
   * Verify if existing [[SessionMod]] & Report its activity. (Exception thrown if no session can be retrieved)
   *
   * @param authTokenToVerify
   *   The brut authorization token
   * @return
   *   The identified session
   */
  def verifyAuthorization(authTokenToVerify: String): IO[SessionMod] = for {
    _       <- SessionMod.refreshWithAuthToken(authTokenToVerify)
    session <- SessionMod.getWithAuthToken(authTokenToVerify)
  } yield session

  /**
   * Create a new [[SessionMod]].
   *
   * @return
   *   Session UUID & Authorization token
   */
  def createSession: IO[SessionAuthEnt] = for {
    // Generate authorization token
    authToken      <-
      (UUIDGen[IO].randomUUID, Clock[IO].monotonic) // `Clock[IO].monotonic` == now unix timestamp in nanoseconds
        .mapN((someUUID, nowUnixTimestamp) => s"$someUUID:$nowUnixTimestamp")
        .map(intermediateToken => Base64.getEncoder.encodeToString(intermediateToken.getBytes(StandardCharsets.UTF_8)))

    // Create & Persist the new session
    createdSession <- SessionMod(authToken)
    _              <- createdSession.startCronJobInactivityCheck() // Start also the inactivity checker
  } yield SessionAuthEnt(createdSession.id, authToken)

  /**
   * Terminate the provided session.
   * @param validatedSession
   *   A validated session
   * @return
   *   Session updated status
   */
  def terminateSession(validatedSession: SessionMod): IO[SessionStatusEnt] = for {
    _              <- SessionMod.terminateWithId(validatedSession.id)
    updatedSession <- SessionMod.getWithId(validatedSession.id)
  } yield SessionStatusEnt(updatedSession.id,
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
  def listSessions(validatedSession: SessionMod): IO[Array[SessionStatusEnt]] = for {
    sessions      <- SessionMod.listActiveSessions
    sessionsStatus = sessions.map(session =>
                       SessionStatusEnt(session.id,
                                        session.createdAt.toString,
                                        session.updatedAt.toString,
                                        session.terminatedAt.map(_.toString)))
  } yield sessionsStatus

}
