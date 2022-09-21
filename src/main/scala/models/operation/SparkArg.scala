package com.ilovedatajjia
package models.operation

import cats.effect.IO
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredDecoder
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions._
import services.SparkServer._
import utils.NormType._

/**
 * Operation arguments.
 */
sealed trait SparkArg

/**
 * ADT of [[SparkArg]].
 */
object SparkArg {

  /**
   * Read operation.
   */
  sealed trait SparkArgR extends SparkArg {

    /**
     * Fit the Spark operation.
     * @param input
     *   The input DataFrame
     * @return
     *   Spark [[DataFrame]] with the operation applied
     */
    def fitArg(input: String): IO[DataFrame]

  }

  /**
   * Compute operation.
   */
  sealed trait SparkArgC extends SparkArg {

    /**
     * Fit the Spark operation.
     * @param input
     *   The input DataFrame
     * @return
     *   Spark [[DataFrame]] with the operation applied
     */
    def fitArg(input: DataFrame): IO[DataFrame]

  }

  // For JSON decoding & encoding
  implicit val argConf: Configuration            = Configuration.default.withDiscriminator("_$OP_")
  implicit val argDec: Decoder[SparkArg]         = deriveConfiguredDecoder[SparkArg]
  implicit val argEnc: Encoder[SparkArg]         = deriveConfiguredEncoder[SparkArg]
  implicit val cstmColDec: Decoder[CustomColArg] = deriveDecoder[CustomColArg]
  implicit val cstmColEnc: Encoder[CustomColArg] = deriveEncoder[CustomColArg]

  /**
   * Csv read arguments.
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
  case class CsvArg(sep: String,
                    quote: String,
                    escape: String,
                    header: Boolean,
                    inferSchema: Boolean,
                    dateFormat: Option[String],
                    timestampFormat: Option[String])
      extends SparkArgR {

    /**
     * Fit the Spark operation.
     * @param input
     *   The input DataFrame
     * @return
     *   Spark [[DataFrame]] with the operation applied
     */
    override def fitArg(input: String): IO[DataFrame] = IO {
      // Build read options
      val defaultOptions: Map[String, String]  = Map("mode" -> "FAILFAST", "multiLine" -> "true")
      val parsedOptions: Map[String, String]   = Map("sep" -> sep,
                                                   "quote"       -> quote,
                                                   "escape"      -> escape,
                                                   "header"      -> header.toString,
                                                   "inferSchema" -> inferSchema.toString)
      val optionalOptions: Map[String, String] =
        dateFormat.map(x => Map("dateFormat" -> x)).getOrElse(Map.empty[String, String]) ++
          timestampFormat
            .map(x => Map("timestampFormat" -> x))
            .getOrElse(Map.empty[String, String])
      val readOptions: Map[String, String]     = defaultOptions ++ parsedOptions ++ optionalOptions

      // Build & Return the Spark DataFrame
      import spark.implicits._
      val inputDS: Dataset[String] = spark.createDataset(List(input))
      spark.read.options(readOptions).csv(inputDS)
    }

  }

  /**
   * Json read arguments.
   * @param inferSchema
   *   If infer the DataFrame schema or not
   * @param dateFormat
   *   Date format to consider when inferring the schema
   * @param timestampFormat
   *   Timestamp format to consider when inferring the schema
   */
  case class JsonArg(sep: String,
                     quote: String,
                     escape: String,
                     header: Boolean,
                     inferSchema: Boolean,
                     dateFormat: Option[String],
                     timestampFormat: Option[String])
      extends SparkArgR {

    /**
     * Fit the Spark operation.
     * @param input
     *   The input DataFrame in string representation OR actual Spark DataFrame
     * @return
     *   Spark [[DataFrame]] with the operation applied
     */
    override def fitArg(input: String): IO[DataFrame] = IO {
      // Build read options
      val defaultOptions: Map[String, String]  = Map("mode" -> "FAILFAST", "multiLine" -> "true", "encoding" -> "UTF-8")
      val parsedOptions: Map[String, String]   = Map("primitivesAsString" -> (!inferSchema).toString)
      val optionalOptions: Map[String, String] =
        dateFormat.map(x => Map("dateFormat" -> x)).getOrElse(Map.empty[String, String]) ++
          timestampFormat
            .map(x => Map("timestampFormat" -> x))
            .getOrElse(Map.empty[String, String])
      val readOptions: Map[String, String]     = defaultOptions ++ parsedOptions ++ optionalOptions

      // Build & Return the Spark DataFrame
      import spark.implicits._
      val inputDS: Dataset[String] = spark.createDataset(List(input))
      spark.read.options(readOptions).csv(inputDS)
    }

  }

  /**
   * Custom schema arguments.
   * @param schema
   *   Natural index of the column and its type
   */
  case class CustomSchArg(schema: Array[CustomColArg]) extends SparkArgC {

    /**
     * Apply the Spark operation.
     * @param input
     *   The input DataFrame
     * @return
     *   Spark [[DataFrame]] with the operation applied
     */
    override def fitArg(input: DataFrame): IO[DataFrame] = IO {
      // Replace natural index by actual names
      val inputColNames: Array[String]                         = input.columns
      val nbCols: Int                                          = inputColNames.length
      val natIdxRep: Array[(String, NormType, Option[String])] = schema.collect {
        case CustomColArg(natIdx, normType, fmt) if natIdx < nbCols => (inputColNames(natIdx), normType, fmt)
      }

      // Build the output columns
      val inputOutCols: Array[Column] = natIdxRep.collect {
        case (natColName: String, Date, Some(fmt))                                           =>
          to_date(input(natColName), fmt).as(natColName)
        case (natColName: String, Timestamp, Some(fmt))                                      =>
          to_timestamp(input(natColName), fmt).as(natColName)
        case (natColName: String, normType, _) if !Array(Date, Timestamp).contains(normType) =>
          input(natColName).cast(normType.toDataType).as(natColName)
      }

      // Return
      input.select(inputOutCols: _*)
    }

  }

  /**
   * Custom column arguments.
   * @param natIdx
   *   Natural index
   * @param normType
   *   Type
   */
  case class CustomColArg(natIdx: Int, normType: NormType, format: Option[String])

  /**
   * Rich functions for [[Json]].
   * @param x
   *   Applied on
   */
  implicit class RichJson(x: Json) {

    /**
     * Convert [[Json]] to Spark arguments.
     * @return
     *   Array of [[SparkArg]]
     */
    def toSparkArgs: Array[SparkArg] = x.as[Array[SparkArg]] match {
      case Left(x)       => throw x
      case Right(result) => result
    }

  }

}
