package com.ilovedatajjia
package api.dto.input

import api.dto.input.FileImportOptDtoIn._
import api.helpers.NormTypeEnum._
import ut.helpers.CatsEffectSparkMockitoSpec
import cats.implicits._
import cats.effect.IO
import io.circe.parser.parse

/**
 * [[FileImportOptDtoIn]] test(s).
 */
class FileImportOptDtoInTest extends CatsEffectSparkMockitoSpec {

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
        .asserting(_ shouldBe Right(CustomColSchema(0, CustomColBase(Numerical).some, "_c0".some)))
    }
    "**UT2** - CustomSchema JSON can be decoded without \"newColType\"" in {
      IO(parse("""{
                 |  "natColIdx": 0,
                 |  "newColName": "_c0"
                 |}""".stripMargin).flatMap(_.as[CustomColSchema]))
        .asserting(_ shouldBe Right(CustomColSchema(0, None, "_c0".some)))
    }
    "**UT3** - CustomSchema JSON can be decoded without \"newColName\"" in {
      IO(parse(
        """{
          |  "natColIdx": 0,
          |  "newColType": {
          |    "nameType": "Numerical"
          |  }
          |}""".stripMargin).flatMap(_.as[CustomColSchema]))
        .asserting(_ shouldBe Right(CustomColSchema(0, CustomColBase(Numerical).some, None)))
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
                 |  "customSchema": [
                 |    {
                 |      "natColIdx": 0,
                 |      "newColType": {
                 |        "nameType": "Numerical"
                 |      },
                 |      "newColName": "_c0"
                 |    }
                 |  ]
                 |}""".stripMargin).flatMap(_.as[FileImportOptDtoIn]))
        .asserting {
          case Left(e) => fail(e)
          case Right(
                output: CsvImportOptDtoIn
              ) => // De-wrap because Option[Array[_]] equality cannot be tested directly
            output.sep shouldBe ","
            output.quote shouldBe "\""
            output.escape shouldBe "\\"
            output.header shouldBe false
            output.inferSchema shouldBe false
            output.customSchema.get shouldBe Array(CustomColSchema(0, CustomColBase(Numerical).some, "_c0".some))
          case _       => fail("Failed because of incorrect type `FileImportOptDtoIn`")
        }
    }
  }

  // JsonImportOptDtoIn test(s)
  "JsonImportOptDtoIn test(s)" - {
    "**UT1** - JSON can be decoded into JsonImportOptDtoIn" in {
      IO(parse("""{
                 |  "inferSchema": false,
                 |  "customSchema": [
                 |    {
                 |      "natColIdx": 0,
                 |      "newColType": {
                 |        "nameType": "Numerical"
                 |      },
                 |      "newColName": "_c0"
                 |    }
                 |  ]
                 |}""".stripMargin).flatMap(_.as[FileImportOptDtoIn]))
        .asserting {
          case Left(e) => fail(e)
          case Right(
                output: JsonImportOptDtoIn
              ) => // De-wrap because Option[Array[_]] equality cannot be tested directly
            output.inferSchema shouldBe false
            output.customSchema.get shouldBe Array(CustomColSchema(0, CustomColBase(Numerical).some, "_c0".some))
          case _       => fail("Failed because of incorrect type `FileImportOptDtoIn`")
        }
    }
  }

}
