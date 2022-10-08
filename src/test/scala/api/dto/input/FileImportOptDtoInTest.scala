package com.ilovedatajjia
package api.dto.input

import api.dto.input.FileImportOptDtoIn._
import api.helpers.CirceExtension.RichString
import api.helpers.NormTypeEnum._
import cats.data.EitherT
import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.propspec.AnyPropSpec
import scala.io.Source

/**
 * [[FileImportOptDtoIn]] test(s).
 */
class FileImportOptDtoInTest extends AnyPropSpec with TableDrivenPropertyChecks with Matchers with AsyncIOSpec {

  // UT1 to UT3
  private val uts = Table(
    ("ut", "path", "expected"),
    ("**UT1** - CustomColBase decoding", "ut1", CustomColBase(Categorical)),
    ("**UT2** - CustomColDate decoding", "ut2", CustomColDate(Date, "yyyy-MM-dd")),
    ("**UT3** - CustomColTimestamp decoding", "ut3", CustomColTimestamp(Timestamp, "yyyy-MM-dd HH:mm:ss.SSSSSS"))
  )
  forAll(uts) { (ut, path, expected) =>
    property(ut) {
      (for {
        rcsPath <- IO(getClass.getResource(s"/api/input/FileImportOptDtoIn/$path").getPath).attemptT
        input   <-
          IO(Source.fromFile(s"$rcsPath/input.json")).bracket(x => IO(x.mkString.toJson))(x => IO(x.close)).attemptT
        output  <- EitherT[CustomColType](IO(input.as[CustomColType]))
      } yield output).asserting(_.value shouldBe Right(expected))
    }
  }

}
