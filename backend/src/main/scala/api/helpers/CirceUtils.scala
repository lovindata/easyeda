package com.ilovedatajjia
package api.helpers

import io.circe._
import java.sql._

/**
 * [[io.circe]] utils.
 */
object CirceUtils {

  // Timestamp
  implicit val timestampEnc: Encoder[Timestamp] = Encoder.instance { x => Json.fromString(x.toString) }
  implicit val timestampDec: Decoder[Timestamp] = Decoder.instance { _.as[String].map(Timestamp.valueOf) }

  // Date
  implicit val dateEnc: Encoder[Date] = Encoder.instance { x => Json.fromString(x.toString) }
  implicit val dateDec: Decoder[Date] = Decoder.instance { _.as[String].map(Date.valueOf) }

}
