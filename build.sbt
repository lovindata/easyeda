/**
 * Project configuration(s)
 */
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"
lazy val root = (project in file("."))
  .settings(name := "easyeda", idePackagePrefix := Some("com.ilovedatajjia"))

/**
 * Dev dependencies
 */
// CatsEffect
// https://mvnrepository.com/artifact/org.typelevel/cats-effect
libraryDependencies += "org.typelevel" %% "cats-effect"    % "3.3.14"
// https://mvnrepository.com/artifact/org.typelevel/log4cats-core
libraryDependencies += "org.typelevel" %% "log4cats-core"  % "2.5.0"
// https://mvnrepository.com/artifact/org.typelevel/log4cats-slf4j
libraryDependencies += "org.typelevel" %% "log4cats-slf4j" % "2.5.0"

// Http4s
// https://mvnrepository.com/artifact/org.http4s/http4s-ember-server
libraryDependencies += "org.http4s"    %% "http4s-ember-server" % "0.23.16"
// https://mvnrepository.com/artifact/org.http4s/http4s-ember-client
libraryDependencies += "org.http4s"    %% "http4s-ember-client" % "0.23.16"
// https://mvnrepository.com/artifact/org.http4s/http4s-circe
libraryDependencies += "org.http4s"    %% "http4s-circe"        % "0.23.16"
// https://mvnrepository.com/artifact/org.http4s/http4s-dsl
libraryDependencies += "org.http4s"    %% "http4s-dsl"          % "0.23.16"
// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
libraryDependencies += "ch.qos.logback" % "logback-classic"     % "1.4.4" % Runtime // Needed otherwise SLF4J will failed

// Circe
// https://mvnrepository.com/artifact/io.circe/circe-parser
libraryDependencies += "io.circe" %% "circe-parser"         % "0.14.3"
// https://mvnrepository.com/artifact/io.circe/circe-generic
libraryDependencies += "io.circe" %% "circe-generic"        % "0.14.3"
// https://mvnrepository.com/artifact/io.circe/circe-generic-extras
libraryDependencies += "io.circe" %% "circe-generic-extras" % "0.14.3"
// https://mvnrepository.com/artifact/io.circe/circe-fs2
libraryDependencies += "io.circe" %% "circe-fs2"            % "0.14.0"

// Jedis
// https://mvnrepository.com/artifact/redis.clients/jedis
libraryDependencies += "redis.clients" % "jedis" % "4.3.1"

// Spark
// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core"  % "3.3.1"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql"   % "3.3.1" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-mllib
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "3.3.1" % "provided"

/**
 * Test dependencies
 */
// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest"     %% "scalatest"                     % "3.2.14"   % Test
// https://mvnrepository.com/artifact/org.scalatestplus/mockito-4-6
libraryDependencies += "org.scalatestplus" %% "mockito-4-6"                   % "3.2.14.0" % Test
// https://mvnrepository.com/artifact/org.typelevel/cats-effect-testing-scalatest
libraryDependencies += "org.typelevel"     %% "cats-effect-testing-scalatest" % "1.4.0"    % Test
