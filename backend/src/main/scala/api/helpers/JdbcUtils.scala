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
   * Test JDBC connection.
   * @param driver
   *   JDBC Driver to use
   * @param dbFullUri
   *   Database URI
   * @param prop
   *   Key-value pair for the connection such as "user", "password", "warehouse", ...
   * @return
   *   [[Boolean]] if connection available
   */
  def testIO(driver: String, dbFullUri: String, prop: (String, String)*): IO[Boolean] =
    connIO(driver, dbFullUri, prop: _*)(conn => IO.interruptible(conn.isValid(5)))

  /**
   * Run SQL script.
   * @param sql
   *   SQL script
   * @param driver
   *   JDBC Driver to use
   * @param dbFullUri
   *   Database URI
   * @param prop
   *   Key-value pair for the connection such as "user", "password"
   * @return
   *   Optional table in string representation according the sql query
   */
  def runSQL(
      sql: String,
      driver: String,
      dbFullUri: String,
      prop: (String, String)*): IO[Option[List[List[Option[String]]]]] = // TODO James - Refactoring into Resource
    connIO(driver, dbFullUri, prop: _*) { conn =>
      for {
        stmt <- IO(conn.createStatement)
        out  <- for {
                  hasResult <- IO(stmt.execute(sql))
                  out       <- if (hasResult) IO.some {
                                 val resSet = stmt.getResultSet
                                 val nbCols = resSet.getMetaData.getColumnCount
                                 var output = List.empty[List[Option[String]]]
                                 while (resSet.next()) output = output :+ (1 to nbCols).toList.map { y =>
                                   Option(resSet.getString(y))
                                 }
                                 output
                               }
                               else IO.none
                } yield out
      } yield out
    }

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
