package com.ilovedatajjia
package api.routes.utils

import api.dto.output.AppFatalThrowableDtoOut
import api.dto.output.AppUnhandledExceptionDtoOut
import api.helpers.AppLayerException
import api.helpers.Http4sExtension._
import cats.data.EitherT
import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

/**
 * Utils for response.
 */
object Response {

  /**
   * Rich functions for [[IO]] result to route [[Response]].
   * @param x
   *   [[IO]] result
   * @tparam A
   *   [[IO]] result type
   */
  implicit class ResponseRichIO[A](x: IO[A]) {

    /**
     * Response the [[IO]] result with a provided status code.
     * @param statusCode
     *   Status code
     * @param w
     *   Make sure to have an existing [[EntityEncoder]] in scope
     * @return
     *   HTTP response with the result
     */
    def toResponse(statusCode: Status)(implicit w: EntityEncoder[IO, A]): IO[Response[IO]] = x.redeemWith(
      // Unexpected cases
      {
        case e: Exception => InternalServerError(AppUnhandledExceptionDtoOut(e))
        case t: Throwable => InternalServerError(AppFatalThrowableDtoOut(t))
      },

      // Expected case
      (result: A) => statusCode.toResponseIOWithDtoOut(result)
    )

  }

  /**
   * Rich functions for [[EitherT]] of [[IO]] result to route [[Response]].
   * @param x
   *   Applied on
   * @tparam A
   *   Result type
   */
  implicit class ResponseRichEitherT[A](x: EitherT[IO, AppLayerException, A]) {

    /**
     * Response the [[IO]] result with a provided status code.
     * @param statusCode
     *   Status code
     * @param w
     *   Make sure to have an existing [[EntityEncoder]] in scope
     * @return
     *   HTTP response with the result OR
     *   - [[InternalServerError]] if any caught exception
     */
    def toResponse(statusCode: Status)(implicit w: EntityEncoder[IO, A]): IO[Response[IO]] = x.value.redeemWith(
      // Unexpected cases
      {
        case e: Exception => InternalServerError(AppUnhandledExceptionDtoOut(e))
        case t: Throwable => InternalServerError(AppFatalThrowableDtoOut(t))
      },

      // Expected cases
      {
        case Left(e)       => e.toResponseIO
        case Right(result) => statusCode.toResponseIOWithDtoOut(result)
      }
    )

  }

}
