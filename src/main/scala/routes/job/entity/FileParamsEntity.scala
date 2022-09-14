package com.ilovedatajjia
package routes.job.entity

import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._
import java.text.SimpleDateFormat

/**
 * For JSON automatic derivation.
 */
sealed trait FileParamsEntity

/**
 * ADT pattern of [[FileParamsEntity]].
 */
object FileParamsEntity {

  // For encoding & decoding
  implicit val fileParamsEntityConfig: Configuration              = Configuration.default.withDiscriminator("class")
  implicit val fileParamsEntityEncoder: Encoder[FileParamsEntity] = deriveConfiguredEncoder
  implicit val fileParamsEntityDecoder: Decoder[FileParamsEntity] = deriveConfiguredDecoder

  /**
   * Rich functions for [[Json]].
   */
  implicit class RichJson(x: Json) {

    /**
     * Decode [[x]] as [[FileParamsEntity]].
     * @return
     *   Decoded applied on object OR [[DecodingFailure]] exception
     */
    def toFileParamsEntity: FileParamsEntity = x.as[FileParamsEntity] match {
      case Left(decError) => throw decError
      case Right(result)  => result
    }

  }

  /**
   * For JSON automatic derivation.
   * @param sep
   *   Separator character(s) for the csv
   * @param quote
   *   Quote character(s) for the csv
   * @param escape
   *   Escape character(s) for the csv
   * @param header
   *   If the csv contains a header or not
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   */
  case class CsvParamsEntity(sep: String,
                             quote: String,
                             escape: String,
                             header: Boolean,
                             inferSchema: Boolean,
                             dateFormat: Option[SimpleDateFormat],
                             timestampFormat: Option[SimpleDateFormat])
      extends FileParamsEntity

  /**
   * For JSON automatic derivation.
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   */
  case class JsonParamsEntity(inferSchema: Boolean,
                              dateFormat: Option[SimpleDateFormat],
                              timestampFormat: Option[SimpleDateFormat])
      extends FileParamsEntity

}
