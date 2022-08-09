package com.ilovedatajjia
package controllers

import cats.effect.IO
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
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
  def createSession: IO[String] = {

    // Generate UUID & Authentication token
    def genUUID: IO[UUID]        = IO(UUID.randomUUID())
    def genAuthToken: IO[String] = IO {
      val intermediateToken: String = s"${System.nanoTime()} ${UUID.randomUUID()}"
      Base64.getEncoder.encodeToString(intermediateToken.getBytes(StandardCharsets.UTF_8))
    }

    // Encode to SHA1 the authentication token
    def encAuthToken(sessionAuthToken: String): IO[Array[Byte]] = IO {
      MessageDigest
        .getInstance("SHA-1")
        .digest(sessionAuthToken.getBytes(StandardCharsets.UTF_8))
    }

    // Create & Persist a new session
    def createPersistSession(sessionUUID: UUID, encodedAuthToken: Array[Byte]): IO[Session] = {
      val newCreatedSession: Session = Session(sessionUUID, encodedAuthToken)
      newCreatedSession.persistSession
    }

    // Compose IOs
    for {
      uuid             <- genUUID
      authToken        <- genAuthToken
      encodedAuthToken <- encAuthToken(authToken)
      _                <- createPersistSession(uuid, encodedAuthToken)
    } yield authToken

  }

}
