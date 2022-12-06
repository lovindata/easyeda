package com.ilovedatajjia
package config

import cats.effect._
import config.ConfigLoader._
import redis.clients.jedis.JedisPooled
import redis.clients.jedis.UnifiedJedis

/**
 * Utils for models.
 */
object DBDriver {

  // Initialize database driver
  val redisDriver: Resource[IO, UnifiedJedis] =
    Resource.make(IO.blocking(new JedisPooled("localhost", dbPort)))((conn: UnifiedJedis) => IO.blocking(conn.close()))

}
