package com.ilovedatajjia
package api.helpers

import doobie._
import io.circe._
import sttp.tapir.Schema

/**
 * Enum of possible connections.
 */
object ConnTypeEnum extends Enumeration {

  // Enum
  type ConnType = Value
  val Postgres: ConnType = Value("postgres")
  val Mongo: ConnType    = Value("mongo")

  // JSON & SwaggerUI
  implicit val enc: Encoder[ConnType] = x => Json.fromString(x.toString)
  implicit val dec: Decoder[ConnType] = _.value.as[String].map(ConnTypeEnum.withName)
  implicit val sch: Schema[ConnType]  = Schema.derivedEnumerationValue

  // Doobie mapping
  implicit val meta: Meta[ConnType] = Meta[String].timap(withName)(_.toString)

}
