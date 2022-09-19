package com.ilovedatajjia
package models.operation

import SparkArg._
import io.circe.Json
import io.circe.parser.parse
import scala.io.Source

object TestDraft extends App {

  val strJson = Source.fromFile("D:\\prog\\proj\\learning-http4s\\src\\main\\resources\\draftImportCSV.json")
  val test    = parse(strJson.mkString).getOrElse(Json.Null).as[Array[SparkArg]]
  println(test)

}
