package com.ilovedatajjia
package routes.job

import cats.effect.IO
import fs2.Stream
import fs2.text
import java.sql.Timestamp
import models.Session
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.multipart.Multipart
import routes.utils.Auth._

/**
 * Routes related to CSV files management.
 */
object CsvRoutes {

  def test(req: Request[IO]): Response[IO] = {

    // Validate header request & Retrieve the body
    val validatedStream: Either[String, Stream[IO, Byte]] = for {
      contentTypeValue <-
        req.headers
          .get[`Content-Type`]
          .toRight("Please verify `Content-Type` header and its value are correctly formatted & provided")
      stream           <- contentTypeValue.mediaType match {
                            case MediaType.multipart.`form-data` =>
                              Right(req.body)
                            case _                               =>
                              Left("Please verify your request contains a multipart/form-data")
                          }
    } yield stream

    // Drain the stream as a String
    ???

  }

  // Define other routes
  private val previewRoute: AuthedRoutes[Session, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "csv" / "preview" as session =>
      // Ok(req.req.body.through(text.utf8.decode).compile.string)
      req.req.decode[Multipart[IO]] { m =>
        val wholeStream   = m.parts.head.body.through(text.utf8.decode)
        // val test          = wholeStream.compile.toList.map(_.length)
        // println("#####")
        // test.map(println)
        val partialStream = wholeStream.take(2)
        Ok(partialStream)
      // Ok(s"""Multipart Data\nParts:${m.parts.head.body.through(text.utf8.decode).compile.string}\n${m.parts
      //    .map(_.name)
      //    .mkString("\n")}""")
      }
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
