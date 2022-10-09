package com.ilovedatajjia
package api.dto.input

import api.dto.input.FileImportOptDtoIn._
import api.helpers.NormTypeEnum._
import cats.effect.IO
import cats.effect.testing.scalatest._
import cats.effect.unsafe.implicits.global.compute
import io.circe.parser.parse
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.ExecutionContext

/**
 * [[FileImportOptDtoIn]] test(s).
 */
class FileImportOptDtoInTest extends AsyncFreeSpec with AsyncIOSpec with Matchers {

  // Global thread pool
  override implicit val executionContext: ExecutionContext = compute

  // CustomColType test(s)
  "CustomColType test(s)" - {
    "**UT1** - JSON can be decoded into CustomColBase" in {
      IO(parse("""{
                 |  "nameType": "Categorical"
                 |}
                 |""".stripMargin).flatMap(_.as[CustomColType])).asserting(_ shouldBe Right(CustomColBase(Categorical)))
    }

    "**UT2** - JSON can be decoded into CustomColDate" in {
      IO(parse("""{
                 |  "nameType": "Date",
                 |  "dateFormat": "yyyy-MM-dd"
                 |}""".stripMargin).flatMap(_.as[CustomColType]))
        .asserting(_ shouldBe Right(CustomColDate(Date, "yyyy-MM-dd")))
    }

    "**UT3** - JSON can be decoded into CustomColTimestamp" in {
      IO(parse("""{
                 |  "nameType": "Timestamp",
                 |  "timestampFormat": "yyyy-MM-dd HH:mm:ss.SSSSSS"
                 |}""".stripMargin).flatMap(_.as[CustomColType]))
        .asserting(_ shouldBe Right(CustomColTimestamp(Timestamp, "yyyy-MM-dd HH:mm:ss.SSSSSS")))
    }
  }

  // CustomSchema test(s)
  "CustomSchema test(s)" - {
    "**UT1** - JSON can be decoded into CustomSchema" in {
      IO(parse("""{
                 |  "natColIdx": 0,
                 |  "newColType": {
                 |    "nameType": "Numerical"
                 |  },
                 |  "newColName": "_c0"
                 |}""".stripMargin).flatMap(_.as[CustomColSchema]))
        .asserting(_ shouldBe Right(CustomColSchema(0, Some(CustomColBase(Numerical)), "_c0")))
    }

    "**UT2** - CustomSchema JSON can be decoded without \"newColType\"" in {
      IO(parse("""{
                 |  "natColIdx": 0,
                 |  "newColName": "_c0"
                 |}""".stripMargin).flatMap(_.as[CustomColSchema]))
        .asserting(_ shouldBe Right(CustomColSchema(0, None, "_c0")))
    }
  }

  // CsvImportOptDtoIn test(s)
  "CsvImportOptDtoIn test(s)" - {
    "**UT1** - JSON can be decoded into CsvImportOptDtoIn" in {
      IO(parse("""{
                 |  "sep": ",",
                 |  "quote": "\"",
                 |  "escape": "\\",
                 |  "header": false,
                 |  "inferSchema": false,
                 |  "customSchema": {
                 |    "natColIdx": 0,
                 |    "newColType": {
                 |      "nameType": "Numerical"
                 |    },
                 |    "newColName": "_c0"
                 |  }
                 |}""".stripMargin).flatMap(_.as[FileImportOptDtoIn]))
        .asserting(
          _ shouldBe Right(
            CsvImportOptDtoIn(",",
                              "\"",
                              "\\",
                              header = false,
                              inferSchema = false,
                              Some(CustomColSchema(0, Some(CustomColBase(Numerical)), "_c0")))))
    }
  }

  // JsonImportOptDtoIn test(s)
  "JsonImportOptDtoIn test(s)" - {
    "**UT1** - JSON can be decoded into JsonImportOptDtoIn" in {
      IO(parse("""{
                 |  "inferSchema": false,
                 |  "customSchema": {
                 |    "natColIdx": 0,
                 |    "newColType": {
                 |      "nameType": "Numerical"
                 |    },
                 |    "newColName": "_c0"
                 |  }
                 |}""".stripMargin).flatMap(_.as[FileImportOptDtoIn]))
        .asserting(_ shouldBe Right(
          JsonImportOptDtoIn(inferSchema = false, Some(CustomColSchema(0, Some(CustomColBase(Numerical)), "_c0")))))
    }
  }

}
