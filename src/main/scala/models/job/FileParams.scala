package com.ilovedatajjia
package models.job

import io.circe.generic.extras.Configuration

/**
 * DB representation of a file parameters.
 */
trait FileParams

/**
 * ADT of [[FileParams]].
 */
object FileParams {

  // Discriminator for encoding & decoding
  implicit val fileParamsConfig: Configuration =
    Configuration.default.withDiscriminator("class")

}
