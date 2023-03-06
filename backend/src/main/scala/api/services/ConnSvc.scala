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
   * @param connForm
   *   Provided connection form
   * @return
   *   [[ConnTestDtoOut]]
   */
  def testConn(connForm: ConnFormDtoIn): IO[ConnTestDtoOut] = (connForm match {
    case PostgresFormDtoIn(_, host, port, dbName, user, pwd)          =>
      JdbcUtils
        .connIO("org.postgresql.Driver", s"jdbc:postgresql://$host:$port/$dbName", "user" -> user, "password" -> pwd)(
          conn => IO.interruptible(conn.isValid(5)))
    case MongoDbFormDtoIn(_, hostPort, dbAuth, replicaSet, user, pwd) =>
      MongoDBUtils.connIO(hostPort.map(x => x.host -> x.port), dbAuth, replicaSet, user, pwd)(conn =>
        IO.interruptible {
          conn
            .getDatabase("sample_airbnb")
            .runCommand(
              new BsonDocument("ping", new BsonInt64(1))
            ) // Verify connection https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/connect/
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
    _       <- IO.raiseUnless(isUp)(
                 AppException("Impossible to connect please verify your external service and provided information."))

    // Create
    connMod <- ConnMod(user.id, form)
  } yield ConnStatusDtoOut(connMod.id, connMod.kind, connMod.name, isUp)

}
