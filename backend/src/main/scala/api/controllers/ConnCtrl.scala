package com.ilovedatajjia
package api.controllers

import api.dto.input.ConnFormDtoIn
import api.dto.input.ConnFormDtoIn._
import api.dto.output.ConnTestDtoOut
import api.dto.output.ConnTestDtoOut._
import api.helpers.JdbcUtils
import cats.effect.IO

/**
 * Controller layer for connection.
 */
object ConnCtrl {

  /**
   * Test connection.
   * @param connForm
   *   Provided connection form
   * @return
   *   [[ConnTestDtoOut]]
   */
  def testConn(connForm: ConnFormDtoIn): IO[ConnTestDtoOut] = connForm match {
    case PostgresFormDtoIn(name, host, port, user, pwd, dbName) =>
      for {
        isUp <- JdbcUtils.connIO("org.postgresql.Driver",
                                 s"jdbc:postgresql://$host:$port/$dbName",
                                 "user"     -> user,
                                 "password" -> pwd)(conn => IO(conn.isValid(5)))
      } yield ConnTestDtoOut(ConnKindEnum.postgres, isUp)
    case MongoDbFormDtoIn(name, hostPort, user, pwd, dbAuth)    => ???
  }

  /**
   * Create connection.
   * @param connForm
   *   Provided connection form
   * @return
   *   All [[ConnStatusDtoOut]]
   */
  def createConn(user: UserMod, conn: ConnFormDtoIn): IO[List[ConnStatusDtoOut]] = for {
    isUp <- testConn(connForm).map(_.isUp)
    _ <- if (isUp) ConnSvc.createConn(form: ConnFormDtoIn)
  } yield ???

}
