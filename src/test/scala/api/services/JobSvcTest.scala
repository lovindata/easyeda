package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn._
import api.dto.output.DataPreviewDtoOut._
import api.helpers.NormTypeEnum._
import fs2.Stream
import java.util.concurrent.TimeoutException
import scala.concurrent.duration.DurationInt
import ut.helpers._

/**
 * [[JobSvc]] test(s).
 */
class JobSvcTest extends CustomCatsEffectSparkSpec {

  // JobSvc.readStream test(s)
  "JobSvc.readStream test(s)" - {
    "**UT1** - Can read csv stream multiple lines (+ Date inferSchema is in Timestamp)" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut1/fileImport.csv", 1)
        outputDf   <- JobSvc
                        .readStream(CsvImportOptDtoIn(",", "\"", "\\", header = false, inferSchema = true, None),
                                    fileImport,
                                    2,
                                    None)
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut1/expected.json",
                                                       "/api/services/JobSvc/readStream/ut1/expected.ddl")
      } yield (outputDf, expectedDf)).value.asserting {
        case Left(e)                       => fail(e)
        case Right((outputDf, expectedDf)) => outputDf shouldBeDataFrame expectedDf
      }
    }
    "**UT2** - Can read csv stream across multiple chunks" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut2/fileImport.csv", 10)
        outputDf   <- JobSvc
                        .readStream(CsvImportOptDtoIn(",", "\"", "\\", header = true, inferSchema = true, None),
                                    fileImport,
                                    2,
                                    None)
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut2/expected.json",
                                                       "/api/services/JobSvc/readStream/ut2/expected.ddl")
      } yield (outputDf, expectedDf)).value.asserting {
        case Left(e)                       => fail(e)
        case Right((outputDf, expectedDf)) => outputDf shouldBeDataFrame expectedDf
      }
    }
    "**UT3** - Can read csv 0 lines" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut3/fileImport.csv", 1)
        outputDf   <- JobSvc
                        .readStream(CsvImportOptDtoIn(",", "\"", "\\", header = false, inferSchema = false, None),
                                    fileImport,
                                    0,
                                    None)
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut3/expected.json",
                                                       "/api/services/JobSvc/readStream/ut3/expected.ddl")
      } yield (outputDf, expectedDf)).value.asserting {
        case Left(e)                       => fail(e)
        case Right((outputDf, expectedDf)) => outputDf shouldBeDataFrame expectedDf
      }
    }
    "**UT4** - Can read csv all lines" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut4/fileImport.csv", 1)
        outputDf   <- JobSvc
                        .readStream(CsvImportOptDtoIn(";", "\"", "\\", header = true, inferSchema = false, None),
                                    fileImport,
                                    -1,
                                    None)
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut4/expected.json",
                                                       "/api/services/JobSvc/readStream/ut4/expected.ddl")
      } yield (outputDf, expectedDf)).value.asserting {
        case Left(e)                       => fail(e)
        case Right((outputDf, expectedDf)) => outputDf shouldBeDataFrame expectedDf
      }
    }
    "**UT5** - Can read json stream multiple lines (+ Date & Timestamp do not work)" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut5/fileImport.json", 10)
        outputDf   <- JobSvc
                        .readStream(JsonImportOptDtoIn(inferSchema = true, None), fileImport, 2, None)
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut5/expected.json",
                                                       "/api/services/JobSvc/readStream/ut5/expected.ddl")
      } yield (outputDf, expectedDf)).value.asserting {
        case Left(e)                       => fail(e)
        case Right((outputDf, expectedDf)) => outputDf shouldBeDataFrame expectedDf
      }
    }
    "**UT6** - Can read all json stream lines" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut6/fileImport.json", 1)
        outputDf   <- JobSvc
                        .readStream(JsonImportOptDtoIn(inferSchema = false, None), fileImport, -1, None)
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut6/expected.json",
                                                       "/api/services/JobSvc/readStream/ut6/expected.ddl")
      } yield (outputDf, expectedDf)).value.asserting {
        case Left(e)                       => fail(e)
        case Right((outputDf, expectedDf)) => outputDf shouldBeDataFrame expectedDf
      }
    }
    "**UT7** - Raise exception when read unknown file options" in {
      JobSvc.readStream(null, Stream.emits(Array.empty[Byte]), 0, None).value.asserting {
        case Left(e)  =>
          e.getClass shouldBe classOf[RuntimeException]
          e.getMessage shouldBe "Unknown matching type for `fileImportOptDtoIn`"
        case Right(_) => fail
      }
    }
    "**UT8** - Can raise timeout exception" in {
      JobSvc
        .readStream(JsonImportOptDtoIn(inferSchema = true, None),
                    Stream.emits(Array.empty[Byte]),
                    0,
                    timeout = Some(0.second))
        .value
        .asserting {
          case Left(e)  =>
            e.getClass shouldBe classOf[TimeoutException]
            e.getMessage shouldBe "0 seconds"
          case Right(_) => fail
        }
    }
  }

  // JobSvc.preview test(s)
  "JobSvc.preview test(s)" - {
    "**UT1** - With limited and multiple rows and columns (all types)" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut1/input.json",
                                                       "/api/services/JobSvc/preview/ut1/input.ddl")
        prevDtoOut <- JobSvc.preview(input, 2, 3, 6, None)
      } yield prevDtoOut).value.asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(2, 4)
          output.dataSchema shouldBe Array(DataSchema("col0", Numerical),
                                           DataSchema("col1", Categorical),
                                           DataSchema("col2", Date),
                                           DataSchema("col3", Timestamp))
          output.dataValues shouldBe Array(Array("", "", "", ""),
                                           Array("0.0", "str0", "2023-01-01", "2023-01-01 00:00:00.000001"))
      }
    }
    "**UT2** - With all rows" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut2/input.json",
                                                       "/api/services/JobSvc/preview/ut2/input.ddl")
        prevDtoOut <- JobSvc.preview(input, -1, 3, 6, None)
      } yield prevDtoOut).value.asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(4, 4)
          output.dataSchema shouldBe Array(DataSchema("col0", Numerical),
                                           DataSchema("col1", Categorical),
                                           DataSchema("col2", Date),
                                           DataSchema("col3", Timestamp))
          output.dataValues shouldBe Array(
            Array("", "", "", ""),
            Array("0.0", "str0", "2023-01-01", "2023-01-01 00:00:00.000001"),
            Array("", "UTSucceedIfInOutput", "", ""),
            Array("", "UTSucceedIfInOutput", "", "")
          )
      }
    }
    "**UT3** - With all columns on the right" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut3/input.json",
                                                       "/api/services/JobSvc/preview/ut3/input.ddl")
        prevDtoOut <- JobSvc.preview(input, 2, 3, -1, None)
      } yield prevDtoOut).value.asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(2, 7)
          output.dataSchema shouldBe Array(
            DataSchema("col0", Numerical),
            DataSchema("col1", Categorical),
            DataSchema("col2", Date),
            DataSchema("col3", Timestamp),
            DataSchema("some_col2_UTSucceedIfInOutput", Categorical),
            DataSchema("some_col3_UTSucceedIfInOutput", Categorical),
            DataSchema("some_col4_UTSucceedIfInOutput", Categorical)
          )
          output.dataValues shouldBe Array(
            Array("", "", "", "", "", "", ""),
            Array("0.0",
                  "str0",
                  "2023-01-01",
                  "2023-01-01 00:00:00.000001",
                  "UTSucceedIfInOutput",
                  "UTSucceedIfInOutput",
                  "UTSucceedIfInOutput")
          )
      }
    }
    "**UT4** - With no columns" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut4/input.json",
                                                       "/api/services/JobSvc/preview/ut4/input.ddl")
        prevDtoOut <- JobSvc.preview(input, 2, -1, 6, None)
      } yield prevDtoOut).value.asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(2, 0)
          output.dataSchema shouldBe Array()
          output.dataValues shouldBe Array(Array(), Array())
      }
    }
    "**UT5** - With all columns" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut5/input.json",
                                                       "/api/services/JobSvc/preview/ut5/input.ddl")
        prevDtoOut <- JobSvc.preview(input, 2, 1, -1, None)
      } yield prevDtoOut).value.asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(2, 7)
          output.dataSchema shouldBe Array(
            DataSchema("some_col0", Categorical),
            DataSchema("some_col1", Categorical),
            DataSchema("col0", Numerical),
            DataSchema("col1", Categorical),
            DataSchema("col2", Date),
            DataSchema("col3", Timestamp),
            DataSchema("some_col2", Categorical)
          )
          output.dataValues shouldBe Array(
            Array("", "", "", "", "", "", ""),
            Array("UTSucceedIfInOutput",
                  "UTSucceedIfInOutput",
                  "0.0",
                  "str0",
                  "2023-01-01",
                  "2023-01-01 00:00:00.000001",
                  "UTSucceedIfInOutput")
          )
      }
    }
    "**UT6** - With exceeding rows and columns" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut6/input.json",
                                                       "/api/services/JobSvc/preview/ut6/input.ddl")
        prevDtoOut <- JobSvc.preview(input, 999, 1, 999, None)
      } yield prevDtoOut).value.asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(1, 1)
          output.dataSchema shouldBe Array(DataSchema("col0", Numerical))
          output.dataValues shouldBe Array(Array("0.0"))
      }
    }
  }

}
