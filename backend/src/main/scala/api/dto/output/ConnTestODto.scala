package com.ilovedatajjia
package api.dto.output

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for testing connection.
 * @param isUp
 *   Connection usable
 * @param err
 *   Error message
 */
case class ConnTestODto(isUp: Boolean, err: Option[String])

/**
 * [[ConnTestODto]] companion.
 */
object ConnTestODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[ConnTestODto] = deriveEncoder
  implicit val dec: Decoder[ConnTestODto] = deriveDecoder
  implicit val sch: Schema[ConnTestODto]  = Schema.derived

}
