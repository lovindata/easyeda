package com.ilovedatajjia
package api.routes.entities

import api.helpers.NormType.NormType
import api.routes.entities.DataPreviewEnt.DataSchema
import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

/**
 * For JSON automatic derivation.
 * @param dataSchema
 *   Schema
 * @param dataValues
 *   Values
 */
case class DataPreviewEnt(dataSchema: Array[DataSchema], dataValues: Array[Array[String]])

/**
 * [[DataPreviewEnt]] companion object with encoders & decoders.
 */
object DataPreviewEnt {

  /**
   * For schema representation.
   * @param colName
   *   Column name
   * @param colType
   *   Column type
   */
  case class DataSchema(colName: String, colType: NormType)

  // JSON encoders & decoders
  implicit val dataSchEncoder: Encoder[DataSchema]  = deriveEncoder
  implicit val dataSchDecoder: Decoder[DataSchema]  = deriveDecoder
  implicit val jsonEncoder: Encoder[DataPreviewEnt] = deriveEncoder
  implicit val jsonDecoder: Decoder[DataPreviewEnt] = deriveDecoder

  // Entity encoder
  implicit val dataSchEntityEncoder: EntityEncoder[IO, DataSchema]      = jsonEncoderOf[IO, DataSchema]
  implicit val dataPrevEntityEncoder: EntityEncoder[IO, DataPreviewEnt] = jsonEncoderOf[IO, DataPreviewEnt]

}
