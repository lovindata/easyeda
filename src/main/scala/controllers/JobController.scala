package com.ilovedatajjia
package controllers

import cats.effect.IO
import io.circe.Json
import models.job.Job
import models.job.Job.JobType._
import models.session.Session
import routes.job.entity.FileParamsEntity
import routes.job.entity.FileParamsEntity.CsvParamsEntity
import routes.job.entity.FileParamsEntity.JsonParamsEntity
import com.ilovedatajjia.models.operation.{ReadJsonOperation, ReadCsvOperation}

/**
 * Controller for jobs logic.
 */
object JobController {

  /**
   * Compute the DataFrame preview of the file using the json parameters.
   * @param validatedSession
   *   Validated session
   * @param fileParamsEntDrained
   *   File parameters in JSON
   * @param fileStrDrained
   *   String representation of the file
   * @return
   *   DataFrame in JSON
   */
  def computePreview(validatedSession: Session,
                     fileParamsEntDrained: IO[FileParamsEntity],
                     fileStrDrained: IO[String]): IO[Json] =
    for {
      // Get file parameters & content
      fileParamsEnt <- fileParamsEntDrained
      fileStr       <- fileStrDrained

      // Starting preview job
      job        <- Job(validatedSession.id, Preview)
      fileParams <- fileParamsEnt match {
                      case csvParEnt: CsvParamsEntity   =>
                        ReadCsvOperation(
                          jobId = job.id,
                          sep = csvParEnt.sep,
                          quote = csvParEnt.quote,
                          escape = csvParEnt.escape,
                          header = csvParEnt.header,
                          inferSchema = csvParEnt.inferSchema,
                          dateFormat = csvParEnt.dateFormat,
                          timestampFormat = csvParEnt.timestampFormat
                        )
                      case jsonParEnt: JsonParamsEntity =>
                        ReadJsonOperation(jobId = job.id,
                                   inferSchema = jsonParEnt.inferSchema,
                                   dateFormat = jsonParEnt.dateFormat,
                                   timestampFormat = jsonParEnt.timestampFormat)
                    }

      // Run preview Job
      _          <- job.run(fileParams, fileStr) // TODO implement the local Spark service & Make the link with your job run

      // Save & Return result

    } yield ???

}
