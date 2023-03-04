package com.ilovedatajjia
package api.helpers

import cats.effect.IO
import java.sql._
import java.util.Properties
import scala.jdk.CollectionConverters._

/**
 * JDBC related utils.
 */
object JdbcUtils {

  /**
   * Run SQL script.
   * @param driver
   *   JDBC Driver to use
   * @param dbFullUrl
   *   Database URL
   * @param prop
   *   Key-value pair for the connection such as "user", "password", "warehouse", ...
   * @return
   *   Optional table in string representation according the sql query
   */
  def connIO[A](driver: String, dbFullUrl: String, prop: (String, String)*)(f: Connection => IO[A]): IO[A] = IO
    .interruptible {
      Class.forName(driver)
      val connProps = new Properties()
      connProps.putAll(prop.toMap.asJava)
      DriverManager.getConnection(dbFullUrl, connProps)
    }
    .bracket(f)(conn => IO.interruptible(conn.close()))

  /**
   * Rich [[String]].
   */
  implicit class RichString(x: String) {

    /**
     * Transform identifier name to be consider as plain name.
     * @param char
     *   Character to use
     * @return
     *   [[String]] identifier name transformed
     */
    def nameB(char: String = "\""): String = s"$char${x.replace(char, s"$char$char")}$char"

  }

}
