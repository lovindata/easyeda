package com.ilovedatajjia
package api.helpers

/**
 * [[doobie]] utils. It also replaces
 * {{{
 * import doobie._
 * import doobie.implicits._
 * import doobie.implicits.javasql._
 * import doobie.postgres.circe.json.implicits._
 * import doobie.postgres.implicits._
 * }}}
 */
object DoobieUtils
    extends doobie.Aliases                             // import doobie._
    with doobie.hi.Modules
    with doobie.free.Modules
    with doobie.free.Types
    with doobie.free.Instances                         // import doobie.implicits._
    with doobie.syntax.AllSyntax
    with doobie.util.meta.SqlMeta
    with doobie.util.meta.TimeMeta
    with doobie.util.meta.LegacyMeta
    with doobie.util.meta.MetaConstructors             // import doobie.implicits.javasql._
    with doobie.util.meta.SqlMetaInstances
    with doobie.postgres.circe.Instances.JsonInstances // import doobie.postgres.circe.json.implicits._
    with doobie.postgres.Instances                     // import doobie.postgres.implicits._
    with doobie.postgres.free.Instances
    with doobie.postgres.JavaTimeInstances
    with doobie.postgres.syntax.ToPostgresMonadErrorOps
    with doobie.postgres.syntax.ToFragmentOps
    with doobie.postgres.syntax.ToPostgresExplainOps
