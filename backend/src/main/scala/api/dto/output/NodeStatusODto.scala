package com.ilovedatajjia
package api.dto.output

import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.Schema

/**
 * DTO for node(s) status.
 * @param nbNodes
 *   Number of nodes
 * @param cpu
 *   Cpu load
 * @param cpuTotal
 *   Cpu total
 * @param ram
 *   Ram load
 * @param ramTotal
 *   Ram total
 */
case class NodeStatusODto(nbNodes: Long, cpu: Double, cpuTotal: Double, ram: Double, ramTotal: Double)

/**
 * [[NodeStatusODto]] companion.
 */
object NodeStatusODto {

  // JSON & SwaggerUI
  implicit val enc: Encoder[NodeStatusODto] = deriveEncoder
  implicit val dec: Decoder[NodeStatusODto] = deriveDecoder
  implicit val sch: Schema[NodeStatusODto]  = Schema.derived

}
