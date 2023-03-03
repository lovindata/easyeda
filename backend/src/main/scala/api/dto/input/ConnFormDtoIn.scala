package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.generic.semiauto.{deriveDecoder => deriveBasedDecoder, deriveEncoder => deriveBasedEncoder, _}
import sttp.tapir.Schema
import sttp.tapir.Schema.SName
import sttp.tapir.generic.{Configuration => TapirConfiguration}

/**
 * DTO for connection creation.
 */
sealed trait ConnFormDtoIn {
  val name: String
}

/**
 * ADT of [[UserFormDtoIn]].
 */
object ConnFormDtoIn       {

  // JSON (de)serializers
  implicit val dbHostPortEnc: Encoder[hostPort] = deriveBasedEncoder
  implicit val dbHostPortDec: Decoder[hostPort] = deriveBasedDecoder
  implicit val confEncDec: Configuration        = Configuration.default.withDiscriminator("kind")
  implicit val enc: Encoder[ConnFormDtoIn]      = deriveConfiguredEncoder
  implicit val dec: Decoder[ConnFormDtoIn]      = deriveConfiguredDecoder

  // Schema serializers
  implicit val hostPortSch: Schema[hostPort] = Schema.derived
  implicit val schConf: TapirConfiguration   = TapirConfiguration.default.withDiscriminator("kind")
  // .copy(toDiscriminatorValue = { case SName(fullName, _) =>
  //   fullName.split("\\.").last
  // })
  implicit val sch: Schema[ConnFormDtoIn]    = Schema.derived

  /**
   * DTO for postgres creation.
   */
  case class PostgresFormDtoIn(name: String, host: String, port: Int, user: String, pwd: String, dbName: String)
      extends ConnFormDtoIn

  /**
   * DTO for mongodb creation.
   */
  case class MongoDbFormDtoIn(name: String, hostPort: List[hostPort], user: String, pwd: String, dbAuth: String)
      extends ConnFormDtoIn

  /**
   * Couple host & port for mongodb.
   */
  case class hostPort(host: String, port: String)

}
