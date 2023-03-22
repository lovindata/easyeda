package com.ilovedatajjia
package api.dto.input

import io.circe._
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.generic.semiauto.{deriveDecoder => deriveBasedDecoder}
import io.circe.generic.semiauto.{deriveEncoder => deriveBasedEncoder}
import sttp.tapir.Schema
import sttp.tapir.generic.{Configuration => TapirConfiguration}

/**
 * DTO for connection creation.
 */
sealed trait ConnFormIDto {

  // Mandatory fields
  val name: String

}

/**
 * ADT of [[UserFormIDto]].
 */
object ConnFormIDto {

  // JSON & SwaggerUI
  implicit val confEncDec: Configuration   = Configuration.default.withDiscriminator("kind")
  implicit val enc: Encoder[ConnFormIDto]  = deriveConfiguredEncoder
  implicit val dec: Decoder[ConnFormIDto]  = deriveConfiguredDecoder
  implicit val schConf: TapirConfiguration = TapirConfiguration.default.withDiscriminator("kind")
  implicit val sch: Schema[ConnFormIDto]   = Schema.derived

  /**
   * DTO for postgres creation.
   */
  case class PostgresFormIDto(name: String, host: String, port: Int, dbName: String, user: String, pwd: String)
      extends ConnFormIDto

  /**
   * DTO for mongodb creation.
   */
  case class MongoFormIDto(name: String,
                           hostPort: List[HostPort],
                           dbAuth: String,
                           replicaSet: String,
                           user: String,
                           pwd: String)
      extends ConnFormIDto

  /**
   * Couple host & port for mongodb.
   */
  case class HostPort(host: String, port: Int)
  object HostPort {
    implicit val hostPortEnc: Encoder[HostPort] = deriveBasedEncoder
    implicit val hostPortDec: Decoder[HostPort] = deriveBasedDecoder
    implicit val hostPortSch: Schema[HostPort]  = Schema.derived
  }

}
