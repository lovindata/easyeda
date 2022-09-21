package com.ilovedatajjia
package services

import cats.effect._
import cats.effect.unsafe.implicits.global.compute
import doobie._
import doobie.hikari.HikariTransactor
import io.circe.Json
import java.sql.Timestamp
import utils.CirceExtension._

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
  implicit val timestampMeta: Meta[Timestamp] = Meta[String].timap[Timestamp](Timestamp.valueOf)(_.toString)
  implicit val jsonMeta: Meta[Json]           = Meta[String].timap[Json](_.toJson)(_.noSpaces)

}
