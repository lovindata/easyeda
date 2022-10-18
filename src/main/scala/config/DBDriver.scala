package com.ilovedatajjia
package config

import api.helpers.CirceExtension._
import cats.effect._
import cats.effect.unsafe.implicits.global.compute
import config.ConfigLoader._
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effect.Log.Stdout._
import doobie._
import doobie.hikari.HikariTransactor
import io.circe.Json

/**
 * Utils for models.
 */
object DBDriver {

  // Initialize database driver
  val redisDriver: Resource[IO, RedisCommands[IO, String, String]] = Redis[IO].utf8(s"redis://localhost:$dbPortRedis")

  // Initialize database driver
  val postgresDriver: Resource[IO, HikariTransactor[IO]] = for {
    transactor <- HikariTransactor.newHikariTransactor[IO]("org.postgresql.Driver",
                                                           s"jdbc:postgresql://localhost:$dbPort/$dbName",
                                                           dbUser,
                                                           dbPwd,
                                                           compute)
  } yield transactor

  // Custom doobie converters when writing into DB sql"""<X>"""
  implicit val jsonMeta: Meta[Json] = Meta[String].timap[Json](_.toJson)(_.noSpaces)

}
