package com.ilovedatajjia
package config

import cats.effect._
import config.ConfigLoader._
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import doobie.implicits._

/**
 * Database driver.
 */
object DBDriver {

  // Resource yielding a transactor configured with a bounded connect EC and an unbounded
  // transaction EC. Everything will be closed and shut down cleanly after use.
  private val transactor: Resource[IO, HikariTransactor[IO]] = for {
    ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
    xa <- HikariTransactor.newHikariTransactor[IO](
            "org.postgresql.Driver",                      // driver classname
            s"jdbc:postgresql://$dbIp:$dbPort/$dbDbName", // connect URL
            s"$dbUser",                                   // username
            s"$dbPwd",                                    // password
            ce                                            // await connection here
          )
  } yield xa

  /**
   * Implicitly using optimized database connection management to run [[doobie]] query. (Not cancellable blocking
   * [[IO]])
   * @param query
   *   [[doobie]] query
   * @tparam A
   *   Output type
   * @return
   *   Output from [[doobie]] query
   */
  def run[A](query: doobie.ConnectionIO[A]): IO[A] = transactor.use(query.transact[IO])

}
