package com.ilovedatajjia
package api.services

import api.dto.input.CreateUserFormDtoIn
import api.dto.output._
import api.helpers.AppLayerException
import api.models.SessionStateEnum._
import api.models.UserMod
import cats.data.EitherT
import cats.effect._
import cats.effect.implicits._
import cats.effect.std.UUIDGen
import cats.implicits._
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Service layer for user.
 */
object UserSvc {

  /**
   * Create the user.
   * @param createUserFormDtoIn
   *   User creation form
   * @return
   *   User status
   */
  def createUser(createUserFormDtoIn: CreateUserFormDtoIn): IO[UserStatusDtoOut] = for {
    user <- UserMod(createUserFormDtoIn)
  } yield UserStatusDtoOut(user.id, user.email)

  /**
   * Setup sessions service.
   */
  def setupSessionSvc: IO[Unit] = for {
    // Restart inactivity checks of existing sessions OR terminate them
    sessions <- UserMod.listSessions(Some(Active))
    _        <- if (continueExistingSessions) {
                  sessions.parTraverse(_.startCronJobInactivityCheck())
                } else {
                  sessions.parTraverse(_.terminate).value.void
                }
  } yield ()

  /**
   * Verify if existing [[UserMod]] and refresh its activity.
   *
   * @param authTokenToVerify
   *   The brut authorization token
   * @return
   *   The identified session OR
   *   - exception from [[UserMod.getWithAuthToken]]
   *   - exception from [[UserMod.refreshStatus]]
   */
  def verifyAuthorization(authTokenToVerify: String): EitherT[IO, AppLayerException, UserMod] = for {
    session         <- UserMod.getWithAuthToken(authTokenToVerify)
    upToDateSession <- session.terminatedAt match { // Refresh only if not terminated
                         case Some(_) => EitherT.right[AppLayerException](IO(session))
                         case None    => session.refreshStatus
                       }
  } yield upToDateSession

  /**
   * Create a new [[UserMod]].
   *
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
    createdSession <- UserMod(authToken)
    _              <- createdSession.startCronJobInactivityCheck() // Start also the inactivity checker
  } yield SessionAuthDtoOut(createdSession.id, authToken)

  /**
   * Terminate the provided session.
   * @param validatedSession
   *   A validated session
   * @return
   *   Session updated status
   */
  def terminateSession(validatedSession: UserMod): EitherT[IO, AppLayerException, SessionStatusDtoOut] = for {
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
    sessions      <- UserMod.listSessions(validatedState)
    sessionsStatus = sessions.map(session =>
                       SessionStatusDtoOut(session.id,
                                           session.createdAt.toString,
                                           session.updatedAt.toString,
                                           session.terminatedAt.map(_.toString)))
  } yield sessionsStatus

}
