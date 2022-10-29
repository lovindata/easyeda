package com.ilovedatajjia
package api.dto.input

import api.helpers.NormTypeEnum.NormType
import cats.syntax.functor._
import io.circe._
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

/**
 * File import options.
 */
sealed trait FileImportOptDtoIn

/**
 * ADT pattern of [[FileImportOptDtoIn]].
 */
object FileImportOptDtoIn {

  // JSON (de)serializers for `CustomColSchema`
  implicit val encCustomColType: Encoder[CustomColType]  = Encoder.instance { _.asJson }
  implicit val decCustomColType: Decoder[CustomColType]  = List[Decoder[CustomColType]](
    Decoder[CustomColDate].widen,
    Decoder[CustomColTimestamp].widen,
    Decoder[CustomColBase].widen // Last because less restrictive
  ).reduceLeft(_ or _)
  implicit val encCustomSchema: Encoder[CustomColSchema] = deriveEncoder
  implicit val decCustomSchema: Decoder[CustomColSchema] = deriveDecoder

  // JSON (de)serializers for `FileImportOptDtoIn`
  implicit val enc: Encoder[FileImportOptDtoIn] = Encoder.instance { _.asJson }
  implicit val dec: Decoder[FileImportOptDtoIn] = List[Decoder[FileImportOptDtoIn]](
    Decoder[CsvImportOptDtoIn].widen,
    Decoder[JsonImportOptDtoIn].widen
  ).reduceLeft(_ or _)

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
   *   Natural index of a column starting at `1`.
   * @param newColType
   *   Optional new custom column type
   * @param newColName
   *   Optional new column name
   */
  case class CustomColSchema(natColIdx: Int, newColType: Option[CustomColType], newColName: Option[String])

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
  case class CsvImportOptDtoIn(sep: String,
                               quote: String,
                               escape: String,
                               header: Boolean,
                               inferSchema: Boolean,
                               customSchema: Option[List[CustomColSchema]])
      extends FileImportOptDtoIn

  /**
   * JSON file options.
   * @param inferSchema
   *   Do the schema inference or not
   * @param customSchema
   *   Custom schema to apply (cannot be used with [[inferSchema]])
   */
  case class JsonImportOptDtoIn(inferSchema: Boolean, customSchema: Option[List[CustomColSchema]])
      extends FileImportOptDtoIn

}
