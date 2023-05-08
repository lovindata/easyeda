package com.ilovedatajjia
package config

import scala.concurrent.duration._

/**
 * Entrypoint for all configuration(s).
 */
object ConfigLoader {

  // Get all environment variables
  private val allEnvVar: Map[String, String] = sys.env

  // Backend conf(s)
  val backEndPort: Int                  = allEnvVar.getOrElse("DATAPIU_BACKEND_PORT", default = "8081").toInt
  val sparkUIPort: Int                  = allEnvVar.getOrElse("DATAPIU_SPARK_UI_PORT", default = "4040").toInt
  val tokenDuration: FiniteDuration     = allEnvVar.getOrElse("DATAPIU_TOKEN_DURATION", default = "3600").toLong.seconds
  val heartbeatInterval: FiniteDuration =
    allEnvVar.getOrElse("DATAPIU_HEARTBEAT_INTERVAL", default = "10").toLong.seconds
  val aliveInterval: FiniteDuration     =
    allEnvVar.getOrElse("DATAPIU_ALIVE_INTERVAL", default = (2 * heartbeatInterval).toSeconds.toString).toLong.seconds

  // Frontend conf(s)
  val backEndOrigin: String        = allEnvVar.getOrElse("DATAPIU_BACKEND_ORIGIN", default = s"http://localhost:$backEndPort")
  val frontEndResourcePath: String = allEnvVar.getOrElse("DATAPIU_FRONTEND_RESOURCES", default = "./frontend/dist")
  val frontEndPort: Int            = allEnvVar.getOrElse("DATAPIU_FRONTEND_PORT", default = "8080").toInt

  // DB conf(s)
  val dbIp: String      = allEnvVar.getOrElse("DATAPIU_DB_IP", default = "localhost")
  val dbPort: Int       = allEnvVar.getOrElse("DATAPIU_DB_PORT", default = "5432").toInt
  val dbDbName: String  = allEnvVar.getOrElse("DATAPIU_DB_DBNAME", default = "elodata_db")
  val dbSchName: String = allEnvVar.getOrElse("DATAPIU_DB_SCHNAME", default = "elodata_sch")
  val dbUser: String    = allEnvVar.getOrElse("DATAPIU_DB_USER", default = "elodata_user")
  val dbPwd: String     = allEnvVar.getOrElse("DATAPIU_DB_PWD", default = "elodata_pwd")

}
