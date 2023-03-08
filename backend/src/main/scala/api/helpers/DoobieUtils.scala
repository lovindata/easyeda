package com.ilovedatajjia
package api.helpers

import cats.implicits._
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax._
import org.postgresql.util.PGobject

/**
 * [[doobie]] utils. It also replaces
 * {{{
 * import doobie._
 * import doobie.implicits._
 * import doobie.implicits.javasql._
 * import doobie.postgres.circe.jsonb.implicits._
 * import doobie.postgres.implicits._
 * }}}
 */
object DoobieUtils
    extends doobie.Aliases                              // import doobie._
    with doobie.hi.Modules
    with doobie.free.Modules
    with doobie.free.Types
    with doobie.free.Instances                          // import doobie.implicits._
    with doobie.syntax.AllSyntax
    with doobie.util.meta.SqlMeta
    with doobie.util.meta.TimeMeta
    with doobie.util.meta.LegacyMeta
    with doobie.util.meta.MetaConstructors              // import doobie.implicits.javasql._
    with doobie.util.meta.SqlMetaInstances
    with doobie.postgres.circe.Instances.JsonbInstances // import doobie.postgres.circe.jsonb.implicits._
    with doobie.postgres.Instances                      // import doobie.postgres.implicits._
    with doobie.postgres.free.Instances
    with doobie.postgres.JavaTimeInstances
    with doobie.postgres.syntax.ToPostgresMonadErrorOps
    with doobie.postgres.syntax.ToFragmentOps
    with doobie.postgres.syntax.ToPostgresExplainOps {

  // Custom mappers
  implicit val metaLJson: Meta[List[Json]] = Meta.Advanced
    .other[PGobject]("jsonb[]")
    .timap[List[Json]](pgObj => parse(pgObj.getValue).leftMap(e => throw e).merge.asArray.get.toList)(lJsonObj => {
      val pgObj = new PGobject
      pgObj.setType("jsonb[]")
      pgObj.setValue(lJsonObj.asJson.noSpaces)
      pgObj
    })

}
