package com.ilovedatajjia
package models.utils

import cats.effect._
import cats.effect.unsafe.implicits.global.compute
import doobie._
import doobie.hikari.HikariTransactor
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Utils for models.
 */
object DBDriver {

  // Initialize database driver
  val mysqlDriver: Resource[IO, HikariTransactor[IO]] = for {
    transactor <- HikariTransactor.newHikariTransactor[IO](
                    "com.mysql.cj.jdbc.Driver",
                    "jdbc:mysql://localhost:3306/restapi",
                    "restapi-user",
                    "restapi-pwd",
                    compute
                  )
  } yield transactor

  // Custom doobie converters when writing into DB sql"""<X>"""
  implicit val timestampMeta: Meta[Timestamp]               =
    Meta[String].timap[Timestamp](Timestamp.valueOf)(_.toString)
  implicit val simpleDateFormatMeta: Meta[SimpleDateFormat] =
    Meta[String].timap[SimpleDateFormat](x => new SimpleDateFormat(x))(_.toString)

}
