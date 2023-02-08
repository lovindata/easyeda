package com.ilovedatajjia
package api.helpers

import java.sql._
import sttp.tapir.Schema

/**
 * [[sttp.tapir]] utils.
 */
object TapirUtils {

  // Schema
  implicit val timestampSch: Schema[Timestamp] = Schema.string
  implicit val dateSch: Schema[Date]           = Schema.string

}
