package com.ilovedatajjia
package config

import api.helpers.CirceExtension._
import cats.effect._
import cats.effect.unsafe.implicits.global.compute
import doobie._
import doobie.hikari.HikariTransactor
import io.circe.Json

/**
 * Utils for models.
 */
object DBDriver {

  // Initialize database driver
  val postgresDriver: Resource[IO, HikariTransactor[IO]] = for {
    transactor <- HikariTransactor.newHikariTransactor[IO](
                    "org.postgresql.Driver",
                    "jdbc:postgresql://localhost:5432/restapi",
                    "restapi-user",
                    "restapi-pwd",
                    compute
                  )
  } yield transactor

  // Custom doobie converters when writing into DB sql"""<X>"""
  implicit val jsonMeta: Meta[Json] = Meta[String].timap[Json](_.toJson)(_.noSpaces)

}
