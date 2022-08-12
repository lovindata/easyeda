/**
 * Project configuration(s)
 */
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"
lazy val root = (project in file("."))
  .settings(name := "learning-http4s", idePackagePrefix := Some("com.ilovedatajjia"))

/**
 * Dev dependencies
 */
// Http4s
// https://mvnrepository.com/artifact/org.http4s/http4s-ember-server
libraryDependencies += "org.http4s"    %% "http4s-ember-server" % "0.23.14"
// https://mvnrepository.com/artifact/org.http4s/http4s-ember-client
libraryDependencies += "org.http4s"    %% "http4s-ember-client" % "0.23.14"
// https://mvnrepository.com/artifact/org.http4s/http4s-circe
libraryDependencies += "org.http4s"    %% "http4s-circe"        % "0.23.14"
// https://mvnrepository.com/artifact/org.http4s/http4s-dsl
libraryDependencies += "org.http4s"    %% "http4s-dsl"          % "0.23.14"
// https://mvnrepository.com/artifact/io.circe/circe-generic
libraryDependencies += "io.circe"      %% "circe-generic"       % "0.14.2"
// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
libraryDependencies += "ch.qos.logback" % "logback-classic"     % "1.2.11" % Runtime // Needed otherwise SLF4J will failed

// Doobie
// https://mvnrepository.com/artifact/org.tpolecat/doobie-core
libraryDependencies += "org.tpolecat" %% "doobie-core"          % "1.0.0-RC2"
// https://mvnrepository.com/artifact/org.tpolecat/doobie-hikari
libraryDependencies += "org.tpolecat" %% "doobie-hikari"        % "1.0.0-RC2"
// https://mvnrepository.com/artifact/mysql/mysql-connector-java
libraryDependencies += "mysql"         % "mysql-connector-java" % "8.0.30"

/**
 * Test dependencies
 */
// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest"           % "3.2.13" % Test
// https://mvnrepository.com/artifact/org.typelevel/munit-cats-effect-2
libraryDependencies += "org.typelevel" %% "munit-cats-effect-2" % "1.0.7"  % Test
