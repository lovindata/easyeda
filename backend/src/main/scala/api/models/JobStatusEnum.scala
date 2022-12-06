package com.ilovedatajjia
package api.models

import io.circe._

/**
 * Possible job status.
 */
object JobStatusEnum extends Enumeration {

  // Main enumerations
  type JobStatus = Value
  val Running, Succeeded, Failed: JobStatus = Value

  // JSON (de)serializers
  implicit val encJobStatus: Encoder[JobStatus] = Encoder.instance(x => Json.fromString(x.toString))
  implicit val decJobStatus: Decoder[JobStatus] = Decoder.instance(_.as[String].map(_.toJobStatus))

  /**
   * Rich functions for [[JobStatus]].
   *
   * @param x
   *   Applied on
   */
  implicit class JobStatusEnumRichString(x: String) {

    /**
     * Get [[JobStatus]] representation of [[x]].
     *
     * @return
     *   [[JobStatus]]
     */
    def toJobStatus: JobStatus = x match {
      case "Running"   => Running
      case "Succeeded" => Succeeded
      case "Failed"    => Failed
      case _           => throw new UnsupportedOperationException(s"Unknown job status `$x`")
    }

  }

}
