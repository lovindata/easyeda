package com.ilovedatajjia
package routes.utils

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

/**
 * Containing rich class related to responses.
 */
object Response {

  /**
   * Rich functions for [[IO]] result to route [[Response]].
   * @param x
   *   [[IO]] result
   * @tparam A
   *   [[IO]] result type
   */
  implicit class RichResponseIO[A](x: IO[A]) {

    /**
     * Response 200 or 500 according the [[IO]] result.
     * @param w
     *   Make sure to have an existing [[EntityEncoder]] in scope
     * @return
     *   HTTP response with the result or Default [[InternalServerError]] with the caught exception message
     */
    def toResponse(implicit w: EntityEncoder[IO, A]): IO[Response[IO]] = x.redeemWith(
      (e: Throwable) => InternalServerError(e.toString),
      (result: A) => Ok(result)
    )

    /**
     * Response 200 or 500 according the [[IO]] result.
     * @param entity
     *   Custom response entity
     * @param w
     *   Make sure to have an existing [[EntityEncoder]] in scope
     * @return
     *   HTTP response with the custom entity or Default [[InternalServerError]] with the caught exception message
     */
    def toResponseWithEntity[B](entity: B)(implicit w: EntityEncoder[IO, B]): IO[Response[IO]] = x.redeemWith(
      (e: Throwable) => InternalServerError(e.toString),
      (_: A) => Ok(entity)
    )

  }

}
