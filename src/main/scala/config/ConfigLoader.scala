package com.ilovedatajjia
package config

import com.comcast.ip4s._
import scala.concurrent.duration._

/**
 * Entrypoint for all configuration(s).
 */
object ConfigLoader {

  // Main services conf(s)
  val appPort: Port       = {
    val parsedPost: Int = sys.env.getOrElse("EASYEDA_PORT", default = "8080").toInt
    case class PortOutOfRangeException(msgException: String) extends Exception
    Port
      .fromInt(parsedPost)
      .getOrElse(throw PortOutOfRangeException("Please ensure your provided port is in range"))
  }
  val sparkUIPort: String = sys.env.getOrElse("EASYEDA_SPARK_UI_PORT", default = "4040")

  // DB conf(s)
  val dbPort: Int = sys.env.getOrElse("EASYEDA_REDIS_PORT", default = "6379").toInt

  // App logic conf(s)
  val maxInactivity: FiniteDuration     = {
    val parsedSeconds: Long = sys.env.getOrElse("EASYEDA_MAX_SESSION_INACTIVITY_SECONDS", default = "3600").toLong
    FiniteDuration(parsedSeconds, SECONDS)
  }
  val continueExistingSessions: Boolean =
    sys.env.getOrElse("EASYEDA_CONTINUE_EXISTING_SESSIONS", default = "true").toBoolean

}
