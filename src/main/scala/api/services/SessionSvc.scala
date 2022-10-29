package com.ilovedatajjia
package api.services

import api.dto.output._
import api.helpers.AppLayerException
import com.ilovedatajjia.api.models.SessionStateEnum._
import api.models.SessionMod
import cats.data.EitherT
import cats.effect._
import cats.effect.implicits._
import cats.effect.std.UUIDGen
import cats.implicits._
import config.ConfigLoader.continueExistingSessions
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Service layer for sessions.
 */
object SessionSvc {

  /**
   * Setup sessions service.
   */
  def setupSessionSvc: IO[Unit] = for {
    // Restart inactivity checks of existing sessions OR terminate them
    sessions <- SessionMod.listSessions(Some(Active))
    _        <- if (continueExistingSessions) {
                  sessions.parTraverse(_.startCronJobInactivityCheck())
                } else {
                  sessions.parTraverse(_.terminate).value.void
                }
  } yield ()

  /**
   * Verify if existing [[SessionMod]] and refresh its activity.
   * @param authTokenToVerify
   *   The brut authorization token
   * @return
   *   The identified session OR
   *   - exception from [[SessionMod.getWithAuthToken]]
   *   - exception from [[SessionMod.refreshStatus]]
   */
  def verifyAuthorization(authTokenToVerify: String): EitherT[IO, AppLayerException, SessionMod] = for {
    session         <- SessionMod.getWithAuthToken(authTokenToVerify)
    upToDateSession <- session.terminatedAt match {
                         case Some(_) => EitherT.right[AppLayerException](IO(session))
                         case None    => session.refreshStatus // Refresh only if not terminated
                       }
  } yield upToDateSession

  /**
   * Create a new [[SessionMod]].
   * @return
   *   Session UUID & Authorization token
   */
  def createSession: IO[SessionAuthDtoOut] = for {
    // Generate authorization token
    authToken      <-
      (UUIDGen[IO].randomUUID, Clock[IO].monotonic) // `Clock[IO].monotonic` == now unix timestamp in nanoseconds
        .mapN((someUUID, nowUnixTimestamp) => s"$someUUID:$nowUnixTimestamp")
        .map(intermediateToken => Base64.getEncoder.encodeToString(intermediateToken.getBytes(StandardCharsets.UTF_8)))

    // Create & Persist the new session
    createdSession <- SessionMod(authToken)
    _              <- createdSession.startCronJobInactivityCheck() // Start also the inactivity checker
  } yield SessionAuthDtoOut(createdSession.id, authToken)

  /**
   * Terminate the provided session.
   * @param validatedSession
   *   A validated session
   * @return
   *   Session updated status
   */
  def terminateSession(validatedSession: SessionMod): EitherT[IO, AppLayerException, SessionStatusDtoOut] = for {
    updatedSession <- validatedSession.terminatedAt match {
                        case Some(_) => EitherT.right[AppLayerException](IO(validatedSession))
                        case None    => validatedSession.terminate // Terminate only if not terminated
                      }
  } yield SessionStatusDtoOut(updatedSession.id,
                              updatedSession.createdAt.toString,
                              updatedSession.updatedAt.toString,
                              updatedSession.terminatedAt.map(_.toString))

  /**
   * List retrievable sessions.
   * @param validatedState
   *   Filtering sessions according a certain state
   * @return
   *   Listing of sessions
   */
  def listSessions(validatedState: Option[SessionStateType]): IO[List[SessionStatusDtoOut]] = for {
    // Starting listing
    sessions      <- SessionMod.listSessions(validatedState)
    sessionsStatus = sessions.map(session =>
                       SessionStatusDtoOut(session.id,
                                           session.createdAt.toString,
                                           session.updatedAt.toString,
                                           session.terminatedAt.map(_.toString)))
  } yield sessionsStatus

}
