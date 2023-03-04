package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.generic.semiauto.{deriveDecoder => deriveBasedDecoder, deriveEncoder => deriveBasedEncoder, _}
import sttp.tapir.Schema
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
  implicit val confEncDec: Configuration   = Configuration.default.withDiscriminator("kind")
  implicit val enc: Encoder[ConnFormDtoIn] = deriveConfiguredEncoder
  implicit val dec: Decoder[ConnFormDtoIn] = deriveConfiguredDecoder

  // Schema serializers
  implicit val schConf: TapirConfiguration = TapirConfiguration.default.withDiscriminator("kind")
  // .copy(toDiscriminatorValue = { case SName(fullName, _) =>
  //   fullName.split("\\.").last
  // })
  implicit val sch: Schema[ConnFormDtoIn]  = Schema.derived

  /**
   * DTO for postgres creation.
   */
  case class PostgresFormDtoIn(name: String, host: String, port: Int, user: String, pwd: String, dbName: String)
      extends ConnFormDtoIn

  /**
   * DTO for mongodb creation.
   */
  case class MongoDbFormDtoIn(name: String, hostPort: List[HostPort], user: String, pwd: String, dbAuth: String)
      extends ConnFormDtoIn

  /**
   * Couple host & port for mongodb.
   */
  case class HostPort(host: String, port: String)
  object HostPort {

    // JSON (de)serializers
    implicit val hostPortEnc: Encoder[HostPort] = deriveBasedEncoder
    implicit val hostPortDec: Decoder[HostPort] = deriveBasedDecoder

    // Schema serializers
    implicit val hostPortSch: Schema[HostPort] = Schema.derived

  }

}
