package com.ilovedatajjia
package api.dto.input

import api.helpers.NormTypeEnum.NormType
import io.circe._
import io.circe.generic.semiauto._

/**
 * For JSON automatic derivation.
 */
sealed trait FileImportOptDtoIn

/**
 * ADT pattern of [[FileImportOptDtoIn]].
 */
object FileImportOptDtoIn {

  // Encoder & Decoder
  implicit val enc: Encoder[FileImportOptDtoIn] = deriveEncoder
  implicit val dec: Decoder[FileImportOptDtoIn] = deriveDecoder

  // Class definition for automatic JSON derivation
  sealed trait CustomColType
  object CustomColType {
    implicit val enc: Encoder[CustomColType] = deriveEncoder
    implicit val dec: Decoder[CustomColType] = deriveDecoder
    case class CustomColBase(nameType: NormType)                               extends CustomColType
    case class CustomColDate(nameType: NormType, dateFormat: String)           extends CustomColType
    case class CustomColTimestamp(nameType: NormType, timestampFormat: String) extends CustomColType
  }

  // Class definition for automatic JSON derivation
  case class CustomSchema(natColIdx: Int, newColType: CustomColType, newColName: String)
  object CustomSchema {
    implicit val enc: Encoder[CustomSchema] = deriveEncoder
    implicit val dec: Decoder[CustomSchema] = deriveDecoder
  }

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

}
