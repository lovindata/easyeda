package com.ilovedatajjia
package api.dto.output

import com.ilovedatajjia.api.dto.output.ConnTestDtoOut.ConnKindEnum
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for testing connection.
 * @param kind
 *   Type of connection
 * @param isUp
 *   Connection usable
 */
case class ConnTestDtoOut(kind: ConnKindEnum.ConnKind, isUp: Boolean)

/**
 * [[ConnTestDtoOut]] companion.
 */
object ConnTestDtoOut {

  // JSON (de)serializers
  implicit val enc: Encoder[ConnTestDtoOut] = deriveEncoder
  implicit val dec: Decoder[ConnTestDtoOut] = deriveDecoder

  // Schema serializer
  implicit val sch: Schema[ConnTestDtoOut] = Schema.derived

  /**
   * Enum of possible connection.
   */
  object ConnKindEnum extends Enumeration {

    // Enum
    type ConnKind = Value
    val postgres: ConnKind = Value("postgres")
    val mongodb: ConnKind  = Value("mongodb")

    // JSON (de)serializers
    implicit val connKindEnc: Encoder[ConnKind] = x => Json.fromString(x.toString)
    implicit val connKindDec: Decoder[ConnKind] = _.value.as[String].map(ConnKindEnum.withName)

    // Schema serializer(s)
    implicit val connKindSch: Schema[ConnKind] = Schema.derivedEnumerationValue

  }

}
