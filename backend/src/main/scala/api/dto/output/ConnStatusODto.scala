package com.ilovedatajjia
package api.dto.output

import api.helpers.ConnTypeEnum
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for connections status.
 * @param id
 *   Connection id
 * @param `type`
 *   Connection type
 * @param name
 *   Connection name
 * @param isUp
 *   Connection status
 */
case class ConnStatusODto(id: Long, `type`: ConnTypeEnum.ConnType, name: String, isUp: Boolean)

/**
 * [[ConnTestODto]] companion.
 */
object ConnStatusODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[ConnStatusODto] = deriveEncoder
  implicit val dec: Decoder[ConnStatusODto] = deriveDecoder
  implicit val sch: Schema[ConnStatusODto]  = Schema.derived

}
