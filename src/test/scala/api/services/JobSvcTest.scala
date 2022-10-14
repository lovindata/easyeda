package com.ilovedatajjia
package api.services

import api.dto.input.FileImportOptDtoIn.CsvImportOptDtoIn
import api.dto.output.DataPreviewDtoOut._
import api.helpers.NormTypeEnum._
import ut.helpers._

/**
 * [[JobSvc]] test(s).
 */
class JobSvcTest extends CustomCatsEffectSparkSpec {

  // JobSvc.readStream test(s)
  "JobSvc.readStream test(s)" - {
    "**UT1** - Can read stream multiple lines (+ Date inferSchema in Timestamp)" in {
      (for {
        fileImport <- Fs2Utils.fromResourceStream("/api/services/JobSvc/readStream/ut1/fileImport.csv", 1)
        outputDf   <- JobSvc
                        .readStream(CsvImportOptDtoIn(",", "\"", "\\", header = false, inferSchema = true, None),
                                    fileImport,
                                    2,
                                    None)
                        .value
        expectedDf <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/readStream/ut1/expected.json",
                                                       "/api/services/JobSvc/readStream/ut1/expected.ddl")
      } yield (outputDf, expectedDf)).asserting {
        case (Left(e), _)                  => fail(e)
        case (Right(outputDf), expectedDf) =>
          println("#####")
          outputDf.printSchema()
          outputDf.show(false)

          println("#####")
          expectedDf.printSchema()
          expectedDf.show(false)

          outputDf shouldBeDataFrame expectedDf
      }
    }
  }

  // JobSvc.preview test(s)
  "JobSvc.preview test(s)" - {
    "**UT1** - With limited and multiple rows and columns (all types)" in {
      (for {
        input      <- SparkUtils.fromResourceDataFrame("/api/services/JobSvc/preview/ut1/input.json",
                                                       "/api/services/JobSvc/preview/ut1/input.ddl")
        prevDtoOut <- JobSvc.preview(input, 2, 3, 6, None).value
      } yield prevDtoOut).asserting {
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
        prevDtoOut <- JobSvc.preview(input, -1, 3, 6, None).value
      } yield prevDtoOut).asserting {
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
        prevDtoOut <- JobSvc.preview(input, 2, 3, -1, None).value
      } yield prevDtoOut).asserting {
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
        prevDtoOut <- JobSvc.preview(input, 2, -1, 6, None).value
      } yield prevDtoOut).asserting {
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
        prevDtoOut <- JobSvc.preview(input, 2, 1, -1, None).value
      } yield prevDtoOut).asserting {
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
        prevDtoOut <- JobSvc.preview(input, 999, 1, 999, None).value
      } yield prevDtoOut).asserting {
        case Left(e)       => fail(e)
        case Right(output) =>
          output.dataConf shouldBe DataConf(1, 1)
          output.dataSchema shouldBe Array(DataSchema("col0", Numerical))
          output.dataValues shouldBe Array(Array("0.0"))
      }
    }
  }

}
