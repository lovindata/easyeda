package com.ilovedatajjia
package models

import java.sql.Timestamp

case class Job(id: Long,
               sessionId: Long,
               jobType: Enumeration,
               status: Enumeration,
               jobParamsId: Long,
               jobResultId: Long,
               createdAt: Timestamp,
               terminatedAt: Timestamp)

object Job {

  object jobType

}
