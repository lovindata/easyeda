package com.ilovedatajjia
package api.routes.conn

import api.helpers.BackendException
import api.helpers.BackendException._
import api.models.UserMod
import api.routes.GenericRts
import api.services.ConnSvc
import cats.effect.IO
import cats.implicits._
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.AnyEndpoint
import sttp.tapir.json.circe._
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

/**
 * Routes for mongo connections management.
 */
object MongoRts extends GenericRts {

  // List databases
  private val listDbEpt: PartialServerEndpoint[String, UserMod, Long, BackendException, List[String], Any, IO] = authEpt
    .summary("list mongo databases")
    .get
    .in("conn" / path[Long]("id") / "mongo" / "databases")
    .out(jsonBody[List[String]])
  private val listDbRts: HttpRoutes[IO]                                                                        =
    Http4sServerInterpreter[IO]().toRoutes(listDbEpt.serverLogic { user => connId =>
      ConnSvc.impl.grantConn(user, connId).flatMap(_.mongo).flatMap(_.listDb).toErrHandled
    })

  // List schemas
  private val listCollEpt
      : PartialServerEndpoint[String, UserMod, (Long, String), BackendException, List[String], Any, IO] =
    authEpt
      .summary("list mongo collections")
      .get
      .in("conn" / path[Long]("id") / "mongo" / path[String]("database") / "collections")
      .out(jsonBody[List[String]])
  private val listCollRts: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(listCollEpt.serverLogic { user =>
    { case (connId, db) => ConnSvc.impl.grantConn(user, connId).flatMap(_.mongo).flatMap(_.listColl(db)).toErrHandled }
  })

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(listDbEpt, listCollEpt).map(_.endpoint)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = listDbRts <+> listCollRts

}
