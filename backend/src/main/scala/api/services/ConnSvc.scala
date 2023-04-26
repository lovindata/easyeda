package com.ilovedatajjia
package api.services

import api.dto.input._
import api.dto.input.ConnFormIDto._
import api.dto.output._
import api.helpers._
import api.helpers.BackendException.AppException
import api.helpers.DoobieUtils._
import api.models._
import cats.effect.IO
import cats.implicits._

/**
 * Service layer for connection.
 */
object ConnSvc {

  /**
   * Test connection.
   * @param form
   *   Provided connection form
   * @return
   *   [[ConnTestODto]]
   */
  def testConn(form: ConnFormIDto): IO[ConnTestODto] = (form match {
    case PostgresFormIDto(_, host, port, dbName, user, pwd)        =>
      JdbcUtils.testIO("org.postgresql.Driver",
                       s"jdbc:postgresql://$host:$port/$dbName",
                       "user"     -> user,
                       "password" -> pwd)
    case MongoFormIDto(_, hostPort, dbAuth, replicaSet, user, pwd) =>
      MongoUtils.testIO(hostPort.map(x => x.host -> x.port), dbAuth, replicaSet, user, pwd)
  }).redeem(_ => ConnTestODto(false), ConnTestODto(_))

  /**
   * Create connection.
   *
   * @param user
   *   User
   * @param form
   *   Provided connection form
   * @return
   *   All [[ConnODto]]
   */
  def createConn(user: UserMod, form: ConnFormIDto): IO[ConnODto] = for {
    // Check connectivity
    isUp    <- testConn(form).map(_.isUp)
    _       <-
      IO.raiseUnless(isUp)(AppException("Impossible to connect verify your external service and provided information."))

    // Create
    connMod <- ConnMod(user.id, form)
  } yield ConnODto(connMod.id, connMod.`type`, connMod.name)

  /**
   * List all connections.
   * @param user
   *   User
   * @return
   *   All connections and their status
   */
  def listConn(user: UserMod): IO[List[ConnODto]] = for {
    allConn <- ConnMod.select(fr"user_id = ${user.id}")
    allDto  <- allConn.traverse { x => IO(ConnODto(x.id, x.`type`, x.name)) }
  } yield allDto

  /**
   * Test known connection.
   * @param user
   *   User
   * @param connId
   *   Connection id
   * @return
   *   [[ConnTestODto]]
   */
  def testKnownConn(user: UserMod, connId: Long): IO[ConnTestODto] = for {
    // Retrieve connection
    allConn <- ConnMod.select(fr"user_id = ${user.id} AND id = $connId")
    conn    <- allConn match {
                 case List(conn) => IO(conn)
                 case List()     => IO.raiseError(AppException("Forbidden connection access or no connection retrievable."))
                 case _          =>
                   IO.raiseError(
                     AppException(s"Corrupted table, incoherent amount (=${allConn.length}) of connections retrieved."))
               }

    // Do test
    dto     <- conn match {
                 case ConnMod(id, _, ConnTypeEnum.Postgres, _) =>
                   for {
                     conn <- ConnPostgresMod.select(fr"conn_id = $id").map(_.head)
                     isUp <- JdbcUtils.testIO("org.postgresql.Driver",
                                              s"jdbc:postgresql://${conn.host}:${conn.port}/${conn.dbName}",
                                              "user"     -> conn.user,
                                              "password" -> conn.pwd)
                   } yield ConnTestODto(isUp)
                 case ConnMod(id, _, ConnTypeEnum.Mongo, _)    =>
                   for {
                     conn     <- ConnMongoMod.select(fr"conn_id = $id").map(_.head)
                     hostPort <- conn.hostPort
                     isUp     <- MongoUtils.testIO(hostPort, conn.dbAuth, conn.replicaSet, conn.user, conn.pwd)
                   } yield ConnTestODto(isUp)
                 case ConnMod(_, _, connType, _)               =>
                   IO.raiseError(AppException(s"Implementation error, unknown connection type $connType."))
               }
  } yield dto

}
