package com.ilovedatajjia
package api.helpers

import io.circe._
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._

/**
 * Extension for data types.
 */
object NormTypeEnum extends Enumeration {

  // Main enumerations
  type NormType = Value
  val Numerical, Categorical, Date, Timestamp: NormType = Value

  // JSON (de)serializers
  implicit val normTypeEnc: Encoder[NormType] = x => Json.fromString(x.toString)
  implicit val normTypeDec: Decoder[NormType] = _.value.as[String].flatMap {
    case x if List("Numerical", "Categorical", "Date", "Timestamp").contains(x) => Right(x.toNormType)
    case x                                                                      => Left(DecodingFailure(s"$x unknown type", List()))
  }

  /**
   * Rich functions for [[DataType]].
   * @param x
   *   Applied on
   */
  implicit class RichDataType(x: DataType) {

    /**
     * Convert [[x]] to normalize.
     * @return
     *   Normalized type
     */
    def toNormType: NormType = x match {
      case _: DoubleType    => NormTypeEnum.Numerical
      case _: StringType    => NormTypeEnum.Categorical
      case _: DateType      => NormTypeEnum.Date
      case _: TimestampType => NormTypeEnum.Timestamp
      case _                => throw new UnsupportedOperationException(s"$x not Spark normalized type")
    }

    /**
     * Convert [[x]] to Spark normalize.
     * @return
     *   Spark normalized type
     */
    def toDataTypeNormType: DataType = x match {
      case _: NumericType              => DoubleType
      case _: BooleanType | StringType => StringType
      case _: DateType | TimestampType => x
      case _                           => throw new UnsupportedOperationException(s"$x unsupported type")
    }

  }

  /**
   * Rich functions for [[NormType]].
   * @param x
   *   Applied on
   */
  implicit class RichNormType(x: NormType) {

    /**
     * Get Spark DataType representation of [[x]].
     * @return
     *   Normalized type
     */
    def toDataType: DataType = x match {
      case Numerical   => DoubleType
      case Categorical => StringType
      case Date        => DateType
      case Timestamp   => TimestampType
    }

    /**
     * Get String representation of [[x]].
     * @return
     *   String representation
     */
    override def toString: String = x match {
      case Numerical   => "Numerical"
      case Categorical => "Categorical"
      case Date        => "Date"
      case Timestamp   => "Timestamp"
    }

  }

  /**
   * Rich functions for [[NormType]].
   * @param x
   *   Applied on
   */
  implicit class NormTypeEnumRichString(x: String) {

    /**
     * Get Spark DataType representation of [[x]].
     * @return
     *   Normalized type
     */
    def toNormType: NormType = x match {
      case "Numerical"   => Numerical
      case "Categorical" => Categorical
      case "Date"        => Date
      case "Timestamp"   => Timestamp
      case _             => throw new UnsupportedOperationException(s"Unknown normalized type `$x`")
    }

  }

  /**
   * Rich functions for [[DataFrame]].
   * @param x
   *   Applied on
   */
  implicit class RichDataFrame(x: DataFrame) {

    /**
     * Convert [[x]] with normalize types.
     * @return
     *   [[x]] DataFrame with normalized types
     */
    def withNormTypes: DataFrame = {
      val xCols: Array[Column] = x.schema.fields.map { case StructField(name, dataType, _, _) =>
        x(name).cast(dataType.toDataTypeNormType).as(name)
      }
      x.select(xCols: _*)
    }

  }

}
