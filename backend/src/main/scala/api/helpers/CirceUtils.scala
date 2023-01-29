package com.ilovedatajjia
package api.helpers

import io.circe._
import java.sql.Timestamp

/**
 * [[io.circe]] utils.
 */
object CirceUtils {

  // Timestamp
  implicit val timestampEnc: Encoder[Timestamp] = Encoder.instance { x => Json.fromString(x.toString) }
  implicit val timestampDec: Decoder[Timestamp] = Decoder.instance { _.as[String].map(Timestamp.valueOf) }

}
