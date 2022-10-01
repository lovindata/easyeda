package com.ilovedatajjia
package api.controllers

import api.controllers.entities._
import api.helpers.CatsEffectExtension.RichArray
import api.helpers.SessionStateEnum._
import api.models.SessionMod
import cats.effect._
import cats.effect.std.UUIDGen
import cats.implicits._
import config.ConfigLoader.continueExistingSessions
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Controller for sessions logic.
 */
object SessionCtrl {

  /**
   * Restart inactivity checks of existing sessions or terminate them.
   */
  def initProcessExistingSessions: IO[Unit] = for {
    sessions <- SessionMod.listSessions(Some(Active))
    _        <- if (continueExistingSessions) {
                  sessions.traverse(_.startCronJobInactivityCheck())
                } else {
                  sessions.traverse(_.terminate)
                }
  } yield ()

  /**
   * Verify if existing [[SessionMod]] and refresh its activity. (Exception thrown if no session can be retrieved)
   * @param authTokenToVerify
   *   The brut authorization token
   * @return
   *   The identified session
   */
  def verifyAuthorization(authTokenToVerify: String): IO[SessionMod] = for {
    session         <- SessionMod.getWithAuthToken(authTokenToVerify)
    upToDateSession <- session.terminatedAt match {
                         case Some(_) => IO(session)
                         case None    => session.refreshStatus // Refresh only if not terminated
                       }
  } yield upToDateSession

  /**
   * Create a new [[SessionMod]].
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
   * Render [[SessionMod]] to [[SessionStatusEnt]].
   * @param validatedSession
   *   Validated session
   * @return
   *   Rendered [[SessionMod]]
   */
  def renderSession(validatedSession: SessionMod): SessionStatusEnt = SessionStatusEnt(
    validatedSession.id,
    validatedSession.createdAt.toString,
    validatedSession.updatedAt.toString,
    validatedSession.terminatedAt.map(_.toString))

  /**
   * Terminate the provided session.
   * @param validatedSession
   *   A validated session
   * @return
   *   Session updated status
   */
  def terminateSession(validatedSession: SessionMod): IO[SessionStatusEnt] = for {
    updatedSession <- validatedSession.terminatedAt match {
                        case Some(_) => IO(validatedSession)
                        case None    => validatedSession.terminate // Terminate only if not terminated
                      }
  } yield SessionStatusEnt(updatedSession.id,
                           updatedSession.createdAt.toString,
                           updatedSession.updatedAt.toString,
                           updatedSession.terminatedAt.map(_.toString))

  /**
   * List all non terminated sessions.
   * @param validatedSession
   *   A validated session
   * @param state
   *   Filtering sessions according a certain state
   * @return
   *   Listing of all non terminated sessions
   */
  def listSessions(validatedSession: SessionMod, state: String): IO[Array[SessionStatusEnt]] = for {
    // Validate the parameter
    filterState   <- IO(state match {
                       case "ALL"             => None
                       case "ACTIVE_ONLY"     => Some(Active)
                       case "TERMINATED_ONLY" => Some(Terminated)
                     })

    // Starting listing
    sessions      <- SessionMod.listSessions(filterState)
    sessionsStatus = sessions.map(session =>
                       SessionStatusEnt(session.id,
                                        session.createdAt.toString,
                                        session.updatedAt.toString,
                                        session.terminatedAt.map(_.toString)))
  } yield sessionsStatus

}
