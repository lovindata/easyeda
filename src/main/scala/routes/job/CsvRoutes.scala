package com.ilovedatajjia
package routes.job

import cats.effect.IO
import fs2.Stream
import fs2.text
import models.Session
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.multipart.Multipart
import org.http4s.multipart.Part
import routes.utils.Auth._

/**
 * Routes related to CSV files management.
 */
object CsvRoutes {

  def test(req: Request[IO]): Response[IO] = {

    // Validate InputStream
    for {
      _ <- req.decode[Multipart[IO]] { m: Multipart[IO] =>
             m.parts.map { p: Part[IO] =>
               val test: Stream[IO, String] = p.body.through(text.utf8.decode)
               test.interr
             }
           }
    } yield ???

  }

  // Define other routes
  private val previewRoute: AuthedRoutes[Session, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "csv" / "preview" as session =>
      ???
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
