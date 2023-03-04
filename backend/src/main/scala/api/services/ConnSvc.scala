package com.ilovedatajjia
package api.services

import api.dto.input._
import api.dto.input.ConnFormDtoIn._
import api.dto.output._
import api.helpers._
import api.models.ConnMod
import api.models.UserMod
import cats.effect.IO

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
    case PostgresFormDtoIn(_, host, port, user, pwd, dbName) =>
      for {
        isUp <- JdbcUtils.connIO("org.postgresql.Driver",
                                 s"jdbc:postgresql://$host:$port/$dbName",
                                 "user"     -> user,
                                 "password" -> pwd)(conn => IO.interruptible(conn.isValid(5)))
      } yield ConnTestDtoOut(isUp)
    case MongoDbFormDtoIn(name, hostPort, user, pwd, dbAuth) => ???
  }).recover(_ => ConnTestDtoOut(false))

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
