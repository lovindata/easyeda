package com.ilovedatajjia
package api.dto.output

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for testing connection.
 * @param isUp
 *   Connection usable
 */
case class ConnTestDtoOut(isUp: Boolean)

/**
 * [[ConnTestDtoOut]] companion.
 */
object ConnTestDtoOut {

  // JSON (de)serializers
  implicit val enc: Encoder[ConnTestDtoOut] = deriveEncoder
  implicit val dec: Decoder[ConnTestDtoOut] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[ConnTestDtoOut] = Schema.derived

}
