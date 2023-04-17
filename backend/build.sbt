/**
 * Project configurations.
 */
ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.10" // https://www.scala-lang.org/download/all.html
lazy val root = (project in file(".")).settings(name := "backend", idePackagePrefix := Some("com.ilovedatajjia"))

/**
 * Manage SBT assembly.
 */
assembly / assemblyJarName       := "elodata-assembly.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
    MergeStrategy.singleOrError // For Tapir https://tapir.softwaremill.com/en/latest/docs/openapi.html
  case _                                                                            => MergeStrategy.preferProject
}

/**
 * Dev dependencies.
 */
// Spark
// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core"  % "3.4.0" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql"   % "3.4.0" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.4.0" % "provided"

// Cats Effect & Scala extensions
// https://mvnrepository.com/artifact/org.typelevel/cats-effect
libraryDependencies += "org.typelevel"              %% "cats-effect" % "3.4.8"
// https://mvnrepository.com/artifact/com.softwaremill.quicklens/quicklens
libraryDependencies += "com.softwaremill.quicklens" %% "quicklens"   % "1.9.1"

// Tapir
// https://mvnrepository.com/artifact/com.softwaremill.sttp.tapir/tapir-http4s-server
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"     % "1.2.12"
// https://mvnrepository.com/artifact/com.softwaremill.sttp.tapir/tapir-json-circe
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % "1.2.12"
// https://mvnrepository.com/artifact/com.softwaremill.sttp.tapir/tapir-swagger-ui-bundle
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.2.12"

// Http4s
// https://mvnrepository.com/artifact/org.http4s/http4s-ember-server
libraryDependencies += "org.http4s" %% "http4s-ember-server" % "0.23.18"
// https://mvnrepository.com/artifact/org.http4s/http4s-circe
libraryDependencies += "org.http4s" %% "http4s-circe"        % "0.23.18"
// https://mvnrepository.com/artifact/org.http4s/http4s-dsl
libraryDependencies += "org.http4s" %% "http4s-dsl"          % "0.23.18"

// Circe
// https://mvnrepository.com/artifact/io.circe/circe-parser
libraryDependencies += "io.circe" %% "circe-parser"         % "0.14.5"
// https://mvnrepository.com/artifact/io.circe/circe-generic
libraryDependencies += "io.circe" %% "circe-generic"        % "0.14.5"
// https://mvnrepository.com/artifact/io.circe/circe-generic-extras
libraryDependencies += "io.circe" %% "circe-generic-extras" % "0.14.3"
// https://mvnrepository.com/artifact/io.circe/circe-literal
libraryDependencies += "io.circe" %% "circe-literal"        % "0.14.5"
// https://mvnrepository.com/artifact/io.circe/circe-fs2
libraryDependencies += "io.circe" %% "circe-fs2"            % "0.14.1"

// Doobie
// https://mvnrepository.com/artifact/org.tpolecat/doobie-core
libraryDependencies += "org.tpolecat" %% "doobie-core"           % "1.0.0-RC2"
// https://mvnrepository.com/artifact/org.tpolecat/doobie-hikari
libraryDependencies += "org.tpolecat" %% "doobie-hikari"         % "1.0.0-RC2"
// https://mvnrepository.com/artifact/org.tpolecat/doobie-postgres
libraryDependencies += "org.tpolecat" %% "doobie-postgres"       % "1.0.0-RC2"
// https://mvnrepository.com/artifact/org.tpolecat/doobie-postgres-circe
libraryDependencies += "org.tpolecat" %% "doobie-postgres-circe" % "1.0.0-RC2"

// Connectors
// https://mvnrepository.com/artifact/org.postgresql/postgresql
libraryDependencies += "org.postgresql"     % "postgresql"         % "42.6.0"
// https://mvnrepository.com/artifact/org.mongodb.scala/mongo-scala-driver
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.1"

// System
// https://mvnrepository.com/artifact/com.github.oshi/oshi-core
libraryDependencies += "com.github.oshi" % "oshi-core" % "6.4.1"

/**
 * Test dependencies.
 */
// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest"     % "3.2.15"  % Test
// https://mvnrepository.com/artifact/org.mockito/mockito-scala
libraryDependencies += "org.mockito"   %% "mockito-scala" % "1.17.14" % Test
