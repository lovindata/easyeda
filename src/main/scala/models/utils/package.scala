package com.ilovedatajjia
package models

import cats.effect._
import cats.effect.unsafe.implicits.global.compute
import doobie._
import doobie.hikari.HikariTransactor
import java.sql.Timestamp
import java.util.UUID

/**
 * Utils for models.
 */
package object utils {

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
  implicit val uuidMeta: Meta[UUID]           =
    Meta[String].timap[UUID](UUID.fromString)(_.toString)
  implicit val timestampMeta: Meta[Timestamp] =
    Meta[String].timap[Timestamp](Timestamp.valueOf)(_.toString)

}
