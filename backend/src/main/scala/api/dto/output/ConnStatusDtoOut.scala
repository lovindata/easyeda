package com.ilovedatajjia
package api.dto.output

import api.helpers.ConnKindEnum
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for connections status.
 * @param id
 *   Connection id
 * @param kind
 *   Connection kind
 * @param name
 *   Connection name
 * @param isUp
 *   Connection status
 */
case class ConnStatusDtoOut(id: Long, kind: ConnKindEnum.ConnKind, name: String, isUp: Boolean)

/**
 * [[ConnTestDtoOut]] companion.
 */
object ConnStatusDtoOut {

  // JSON (de)serializers
  implicit val enc: Encoder[ConnStatusDtoOut] = deriveEncoder
  implicit val dec: Decoder[ConnStatusDtoOut] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[ConnStatusDtoOut] = Schema.derived

}
