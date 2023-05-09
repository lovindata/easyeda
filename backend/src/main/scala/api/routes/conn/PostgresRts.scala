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
 * Routes for postgres connections management.
 */
object PostgresRts extends GenericRts {

  // List databases
  private val listDbEpt: PartialServerEndpoint[String, UserMod, Long, BackendException, List[String], Any, IO] = authEpt
    .summary("list postgres databases")
    .get
    .in("conn" / path[Long]("id") / "postgres" / "databases")
    .out(jsonBody[List[String]])
  private val listDbRts: HttpRoutes[IO]                                                                        =
    Http4sServerInterpreter[IO]().toRoutes(listDbEpt.serverLogic { user => connId =>
      ConnSvc.impl.grantConn(user, connId).flatMap(_.postgres).flatMap(_.listDb).toErrHandled
    })

  // List schemas
  private val listSchEpt
      : PartialServerEndpoint[String, UserMod, (Long, String), BackendException, List[String], Any, IO] =
    authEpt
      .summary("list postgres schemas")
      .get
      .in("conn" / path[Long]("id") / "postgres" / path[String]("database") / "schemas")
      .out(jsonBody[List[String]])
  private val listSchRts: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(listSchEpt.serverLogic { user =>
    { case (connId, db) =>
      ConnSvc.impl.grantConn(user, connId).flatMap(_.postgres).flatMap(_.listSch(db)).toErrHandled
    }
  })

  // List tables
  private val listTabEpt
      : PartialServerEndpoint[String, UserMod, (Long, String, String), BackendException, List[String], Any, IO] =
    authEpt
      .summary("list postgres tables")
      .get
      .in("conn" / path[Long]("id") / "postgres" / path[String]("database") / path[String]("schema") / "tables")
      .out(jsonBody[List[String]])
  private val listTabRts: HttpRoutes[IO] = Http4sServerInterpreter[IO]().toRoutes(listTabEpt.serverLogic { user =>
    { case (connId, db, sch) =>
      ConnSvc.impl.grantConn(user, connId).flatMap(_.postgres).flatMap(_.listTab(db, sch)).toErrHandled
    }
  })

  /**
   * Get all endpoints.
   * @return
   *   Concatenated endpoints
   */
  override def docEpt: List[AnyEndpoint] = List(listDbEpt, listSchEpt, listTabEpt).map(_.endpoint)

  /**
   * Get all applicative routes.
   * @return
   *   Concatenated routes
   */
  override def appRts: HttpRoutes[IO] = listDbRts <+> listSchRts <+> listTabRts

}
