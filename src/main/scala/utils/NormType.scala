package com.ilovedatajjia
package utils

import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types._

/**
 * Extension for data types.
 */
object NormType extends Enumeration {

  // Possible normalized types
  type NormType = Value
  val Numerical, Categorical, Date, Timestamp: NormType = Value

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
      case _: DoubleType    => NormType.Numerical
      case _: StringType    => NormType.Categorical
      case _: DateType      => NormType.Date
      case _: TimestampType => NormType.Timestamp
      case _                => throw new UnsupportedOperationException(s"$x not Spark normalize type")
    }

    /**
     * Convert [[x]] to Spark normalize.
     * @return
     *   Spark normalized type
     */
    def toDataTypeNormType: DataType = x match {
      case _: NumericType              => DoubleType
      case _: BooleanType | StringType => StringType
      case x: DateType | TimestampType => x
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

  }

  /**
   * Rich functions for [[NormType]].
   * @param x
   *   Applied on
   */
  implicit class RichString(x: String) {

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
      case _             => throw new UnsupportedOperationException(s"$x unknown `NormType`")
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
