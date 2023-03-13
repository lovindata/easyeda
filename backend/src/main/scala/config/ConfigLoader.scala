package com.ilovedatajjia
package config

/**
 * Entrypoint for all configuration(s).
 */
object ConfigLoader {

  // Get all environment variables
  private val allEnvVar: Map[String, String] = sys.env

  // Main server conf
  val frontEndResourcePath: String = allEnvVar.getOrElse("FRONTEND_RESOURCES", default = "./frontend/dist")
  val frontEndPort: Int            = allEnvVar.getOrElse("FRONTEND_PORT", default = "8080").toInt
  val backEndPort: Int             = allEnvVar.getOrElse("BACKEND_PORT", default = "8081").toInt
  val sparkUIPort: Int             = allEnvVar.getOrElse("SPARK_UI_PORT", default = "4040").toInt
  val tokenDuration: Long          = allEnvVar.getOrElse("TOKEN_DURATION", default = Long.MaxValue.toString).toLong // In seconds

  // DB conf
  val dbIp: String      = allEnvVar.getOrElse("DB_IP", default = "localhost")
  val dbPort: Int       = allEnvVar.getOrElse("DB_PORT", default = "5432").toInt
  val dbDbName: String  = allEnvVar.getOrElse("DB_DBNAME", default = "elodata_db")
  val dbSchName: String = allEnvVar.getOrElse("DB_SCHNAME", default = "elodata_sch")
  val dbUser: String    = allEnvVar.getOrElse("DB_USER", default = "elodata_user")
  val dbPwd: String     = allEnvVar.getOrElse("DB_PWD", default = "elodata_pwd")

}
