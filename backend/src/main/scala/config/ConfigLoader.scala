package com.ilovedatajjia
package config

/**
 * Entrypoint for all configuration(s).
 */
object ConfigLoader {

  // Get all environment variables
  private val allEnvVar: Map[String, String] = sys.env

  // Main servers conf
  val frontEndResourcePath: String = allEnvVar.getOrElse("FRONTEND_RESOURCES", default = "./frontend/build")
  val frontEndPort: String         = allEnvVar.getOrElse("FRONTEND_PORT", default = "8080")
  val backEndPort: String          = allEnvVar.getOrElse("BACKEND_PORT", default = "8081")
  val sparkUIPort: String          = allEnvVar.getOrElse("SPARK_UI_PORT", default = "4040")

  // DB conf
  val dbIp: String      = allEnvVar.getOrElse("DB_IP", default = "localhost")
  val dbPort: String    = allEnvVar.getOrElse("DB_PORT", default = "5432")
  val dbDbName: String  = allEnvVar.getOrElse("DB_DBNAME", default = "elodata")
  val dbSchName: String = allEnvVar.getOrElse("DB_SCHNAME", default = "elodata_sch")
  val dbUser: String    = allEnvVar.getOrElse("DB_USER", default = "elodata_user")
  val dbPwd: String     = allEnvVar.getOrElse("DB_PWD", default = "elodata_pwd")

}
