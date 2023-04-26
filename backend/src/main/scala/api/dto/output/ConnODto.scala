package com.ilovedatajjia
package api.dto.output

import api.helpers.ConnTypeEnum
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for a connection.
 * @param id
 *   Connection id
 * @param `type`
 *   Connection type
 * @param name
 *   Connection name
 */
case class ConnODto(id: Long, `type`: ConnTypeEnum.ConnType, name: String)

/**
 * [[ConnTestODto]] companion.
 */
object ConnODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[ConnODto] = deriveEncoder
  implicit val dec: Decoder[ConnODto] = deriveDecoder
  implicit val sch: Schema[ConnODto]  = Schema.derived

}
