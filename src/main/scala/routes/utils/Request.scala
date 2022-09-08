package com.ilovedatajjia
package routes.utils

import cats.effect.IO
import cats.implicits.catsSyntaxTuple2Semigroupal
import fs2.Stream
import fs2.text
import io.circe.Json
import io.circe.fs2._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.multipart.Multipart
import org.http4s.multipart.Part

/**
 * Containing rich class related to requests.
 */
object Request {

  /**
   * Extensions for processing request.
   * @param req
   *   Request to process
   */
  implicit class RichRequestIO(req: Request[IO]) {

    /**
     * Process file upload with its corresponding json parameters directly in-memory.
     * @param jsonPartName
     *   JSON parameters part name (the part is supposed in utf8 json)
     * @param filePartName
     *   File uploaded part name (the part is supposed in utf8 text)
     * @param partial
     *   If partial drain of the uploaded file
     * @param f
     *   Execution from the correctly drained parts to the final HTTP response
     * @return
     *   HTTP response from the execution OR un-processable entity response
     */
    def withJSONAndFileBytesMultipart(jsonPartName: String, filePartName: String, partial: Boolean = true)(
        f: (Json, String) => IO[Response[IO]]): IO[Response[IO]] =
      req.req.decode[Multipart[IO]] { multiPart: Multipart[IO] =>
        // Retrieve the byte streams
        val streams: Map[String, Stream[IO, Byte]] = multiPart.parts.collect { part: Part[IO] =>
          (part.name, part.contentType) match {
            case (Some(`jsonPartName`), Some(contentType))
                if contentType.mediaType.satisfies(MediaType.application.json) =>
              "jsonPart" -> part.body
            case (Some(`filePartName`), Some(contentType))
                if contentType.mediaType.satisfies(MediaType.multipart.`form-data`) =>
              "filePart" -> part.body
            case _ =>
              return UnprocessableEntity(
                s"Please verify your request body contains only `$jsonPartName` (application/json) " +
                  s"and `$filePartName` (multipart/form-data)")
          }
        }.toMap

        // Drain streams
        val jsonDrained: IO[Json]   =
          streams("jsonPart").fold("")(_ + _).through(stringStreamParser).compile.lastOrError
        val fileDrained: IO[String] = if (partial) {
          streams("filePart").through(text.utf8.decode).take(1).compile.string
        } else {
          streams("filePart").through(text.utf8.decode).compile.string
        }

        // Return
        for {
          json <- jsonDrained
          file <- fileDrained
        } yield f(json, file)
      }

  }

}