package com.ilovedatajjia
package api.services

import api.dto.input._
import api.dto.input.ConnFormDtoIn._
import api.dto.output._
import api.helpers._
import api.models._
import cats.effect.IO
import org.bson._

/**
 * Service layer for connection.
 */
object ConnSvc {

  /**
   * Test connection.
   * @param form
   *   Provided connection form
   * @return
   *   [[ConnTestDtoOut]]
   */
  def testConn(form: ConnFormDtoIn): IO[ConnTestDtoOut] = (form match {
    case PostgresFormDtoIn(_, host, port, dbName, user, pwd)          =>
      JdbcUtils
        .connIO("org.postgresql.Driver", s"jdbc:postgresql://$host:$port/$dbName", "user" -> user, "password" -> pwd)(
          conn => IO.interruptible(conn.isValid(5)))
    case MongoDbFormDtoIn(_, hostPort, dbAuth, replicaSet, user, pwd) =>
      MongoDbUtils.connIO(hostPort.map(x => x.host -> x.port), dbAuth, replicaSet, user, pwd)(conn =>
        IO.interruptible {
          conn
            .getDatabase(dbAuth)
            .runCommand(
              new BsonDocument("ping", new BsonInt64(1))
            ) // Verify connection https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/connect/
          // & https://www.mongodb.com/docs/manual/reference/command/ping/
          true
        })
  }).redeem(_ => ConnTestDtoOut(false), ConnTestDtoOut(_))

  /**
   * Create connection.
   * @param form
   *   Provided connection form
   * @return
   *   All [[ConnStatusDtoOut]]
   */
  def createConn(user: UserMod, form: ConnFormDtoIn): IO[ConnStatusDtoOut] = for {
    // Check connectivity
    isUp    <- testConn(form).map(_.isUp)
    _       <-
      IO.raiseUnless(isUp)(AppException("Impossible to connect verify your external service and provided information."))

    // Create
    connMod <- ConnMod(user.id, form)
  } yield ConnStatusDtoOut(connMod.id, connMod.`type`, connMod.name, isUp)

}
