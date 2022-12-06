package com.ilovedatajjia
package api.routes.utils

import cats.effect.IO
import cats.implicits._
import fs2.Stream
import fs2.text
import io.circe.Json
import io.circe.fs2.stringStreamParser
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
     * Process CSV or JSON file upload with its corresponding json parameters directly in-memory.
     * @param jsonPartName
     *   JSON parameters part name (the part is supposed in utf8 json)
     * @param fileBytesPartName
     *   File uploaded part name (the part is supposed in utf8 text)
     * @param f
     *   Execution from the correctly drained parts to the final HTTP response
     * @return
     *   HTTP response from the execution `f` OR un-processable entity response
     */
    def withJSONAndFileBytesMultipart(jsonPartName: String, fileBytesPartName: String)(
        f: (Json, Stream[IO, Byte], String) => IO[Response[IO]]): IO[Response[IO]] = req.decode[Multipart[IO]] {
      multiPart: Multipart[IO] =>
        // Retrieve parts from the Multipart[IO]
        val streams: Map[String, (Stream[IO, Byte], Option[String])] = multiPart.parts
          .collect {
            case part: Part[IO] if part.name.isDefined /* && part.contentType.isDefined
            (SwaggerUI issue cannot send with MediaType) */ =>
              (part.name.get, part.contentType, part.body, part.filename)
          }
          .collect {
            case (`jsonPartName`,
                  _,
                  jsonPartBody,
                  None
                ) /* if contentType.mediaType.satisfies(MediaType.application.json)
              (SwaggerUI issue cannot send with MediaType) */ =>
              "jsonPart" -> (jsonPartBody, None)
            case (`fileBytesPartName`, Some(contentType), fileBytesBody, Some(fileName))
                if contentType.mediaType.satisfies(MediaType.text.csv) || contentType.mediaType.satisfies(
                  MediaType.application.json) =>
              "fileBytesPart" -> (fileBytesBody, Some(fileName))
          }
          // If duplicated `jsonPartName` or `fileBytesPartName` than first one defined only
          // (= will take only the latest tuple if duplicated keys)
          .reverse
          .toMap

        // Return according if successful retrieve
        if (!(streams.contains("jsonPart") && streams.contains("fileBytesPart"))) {
          // `f` ignored
          UnprocessableEntity(
            s"Please ensure there are two parts `$jsonPartName` " +
              s"and `$fileBytesPartName` (in `text/csv` or `application/json`)")
        } else {
          // Drain byte from streams
          val jsonDrained: IO[Json]             =
            streams("jsonPart")._1.through(text.utf8.decode).foldMonoid.through(stringStreamParser).compile.lastOrError
          val fileBytesStream: Stream[IO, Byte] = streams("fileBytesPart")._1
          val fileName: String                  = streams("fileBytesPart")._2.get // Sure defined (_.collect + case above)

          // Apply `f`
          jsonDrained.redeemWith(
            (e: Throwable) =>
              UnprocessableEntity(s"Please ensure the part `$jsonPartName` is parsable in json (${e.toString})"),
            { json => f(json, fileBytesStream, fileName) }
          )
        }
    }

  }

}
