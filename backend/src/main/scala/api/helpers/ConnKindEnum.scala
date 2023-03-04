package com.ilovedatajjia
package api.helpers

import doobie._
import io.circe._
import sttp.tapir.Schema

/**
 * Enum of possible connections.
 */
object ConnKindEnum extends Enumeration {

  // Enum
  type ConnKind = Value
  val postgres: ConnKind = Value("postgres")
  val mongodb: ConnKind  = Value("mongodb")

  // JSON (de)serializers
  implicit val enc: Encoder[ConnKind] = x => Json.fromString(x.toString)
  implicit val dec: Decoder[ConnKind] = _.value.as[String].map(ConnKindEnum.withName)

  // Schema serializer(s)
  implicit val sch: Schema[ConnKind] = Schema.derivedEnumerationValue

  // Doobie mapping
  implicit val meta: Meta[ConnKind] = Meta[String].timap(withName)(_.toString)

}
