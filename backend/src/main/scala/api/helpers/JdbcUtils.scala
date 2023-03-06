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
   * Auto-closable provided connection to run an execution.
   * @param driver
   *   JDBC Driver to use
   * @param dbFullUri
   *   Database URI
   * @param prop
   *   Key-value pair for the connection such as "user", "password", "warehouse", ...
   * @param f
   *   Runnable
   * @tparam A
   *   Output datatype
   * @return
   *   Output from runnable
   */
  def connIO[A](driver: String, dbFullUri: String, prop: (String, String)*)(f: Connection => IO[A]): IO[A] = IO
    .interruptible {
      Class.forName(driver)
      val connProps = new Properties()
      connProps.putAll(prop.toMap.asJava)
      DriverManager.getConnection(dbFullUri, connProps)
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
