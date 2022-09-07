package com.ilovedatajjia
package routes.job

import cats.effect.IO
import fs2.Stream
import fs2.text
import io.circe.fs2._
import io.circe.generic.auto._
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
object JobRoutes {

  /*
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
   */

  // Define other routes
  private val previewRoute: AuthedRoutes[Session, IO] = AuthedRoutes.of {
    case req @ POST -> Root / "preview" as session =>
      // Request with `multipart/form-data`
      req.req.decode[Multipart[IO]] { m =>
        val wholeStream = m.parts
          .find { x =>
            println("#################################################")
            println(x.contentType.get.mediaType.satisfies(MediaType.application.json))
            println("#################################################")
            x.name.get == "params"
          }
          .get
          .body
          .through(text.utf8.decode)

        /*
        val wholeStreamFile = m.parts
          .find { x =>
            println("#################################################")
            println(x.contentType.get.mediaType.satisfies(MediaType.application.json))
            println("#################################################")
            x.name.get == "bytes"
          }
          .get
          .body
          .through(text.utf8.decode)
          .compile
          .drain

         */

        val partialStream = wholeStream.through(stringStreamParser).compile.lastOrError
        Ok(partialStream)
      // Ok(s"""Multipart Data\nParts:${m.parts.head.body.through(text.utf8.decode).compile.string}\n${m.parts
      //    .map(_.name)
      //    .mkString("\n")}""")
      }
  }

  // Merge all routes
  val routes: HttpRoutes[IO] = withAuth(previewRoute) // Always the non-auth routes first

}
