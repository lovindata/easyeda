package com.ilovedatajjia
package api.dto.input

import api.helpers.NormTypeEnum.NormType
import io.circe._
import io.circe.generic.semiauto._

/**
 * File import options.
 */
sealed trait FileImportOptDtoIn

/**
 * ADT pattern of [[FileImportOptDtoIn]].
 */
object FileImportOptDtoIn {

  /**
   * Custom column type.
   */
  sealed trait CustomColType
  case class CustomColBase(nameType: NormType)                               extends CustomColType
  case class CustomColDate(nameType: NormType, dateFormat: String)           extends CustomColType
  case class CustomColTimestamp(nameType: NormType, timestampFormat: String) extends CustomColType

  /**
   * Custom schema.
   * @param natColIdx
   *   Natural index of a column starting from `0`.
   * @param newColType
   *   Custom column type
   * @param newColName
   *   New column name
   */
  case class CustomSchema(natColIdx: Int, newColType: CustomColType, newColName: String)

  /**
   * CSV file options.
   * @param sep
   *   Character for CSV column separation
   * @param quote
   *   Character for CSV cell bordering
   * @param escape
   *   Character prefix to consider the next character as character
   * @param header
   *   Existing header or not
   * @param inferSchema
   *   Do the schema inference or not
   * @param customSchema
   *   Custom schema to apply (cannot be used with [[inferSchema]])
   */
  case class CsvImportOptDtoIn(sep: Char,
                               quote: Char,
                               escape: Char,
                               header: Boolean,
                               inferSchema: Boolean,
                               customSchema: Option[CustomSchema])
      extends FileImportOptDtoIn

  /**
   * JSON file options.
   * @param inferSchema
   *   Do the schema inference or not
   * @param customSchema
   *   Custom schema to apply (cannot be used with [[inferSchema]])
   */
  case class JsonImportOptDtoIn(inferSchema: Boolean, customSchema: Option[CustomSchema]) extends FileImportOptDtoIn

  // JSON encoders & decoders
  implicit val encCustomColType: Encoder[CustomColType] = deriveEncoder
  implicit val decCustomColType: Decoder[CustomColType] = deriveDecoder
  implicit val encCustomSchema: Encoder[CustomSchema]   = deriveEncoder
  implicit val decCustomSchema: Decoder[CustomSchema]   = deriveDecoder
  implicit val enc: Encoder[FileImportOptDtoIn]         = deriveEncoder
  implicit val dec: Decoder[FileImportOptDtoIn]         = deriveDecoder

}
