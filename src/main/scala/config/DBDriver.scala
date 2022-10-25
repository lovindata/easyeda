package com.ilovedatajjia
package config

//import api.helpers.CirceExtension._
import cats.effect._
//import cats.effect.unsafe.implicits.global.compute
//import config.ConfigLoader._
//import doobie._
//import doobie.hikari.HikariTransactor
//import io.circe.Json
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.UnifiedJedis

/**
 * Utils for models.
 */
object DBDriver {

  // Initialize database driver
  val redisDriver: Resource[IO, UnifiedJedis] =
    Resource.make(IO.blocking(new JedisPooled("localhost", 6379)))((conn: UnifiedJedis) => IO.blocking(conn.close()))

  /*
  // Initialize database driver (@deprecated)
  val postgresDriver: Resource[IO, HikariTransactor[IO]] = for {
    transactor <- HikariTransactor.newHikariTransactor[IO]("org.postgresql.Driver",
                                                           s"jdbc:postgresql://localhost:$dbPort/$dbName",
                                                           dbUser,
                                                           dbPwd,
                                                           compute)
  } yield transactor

  // Custom doobie converters when writing into DB sql"""<X>""" (@deprecated)
  implicit val jsonMeta: Meta[Json] = Meta[String].timap[Json](_.toJson)(_.noSpaces)
   */

}
