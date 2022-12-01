package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty

import scala.concurrent.duration.{DurationLong, FiniteDuration}

case class TaskStatus(
                       total: Long,
                       updated: Long,
                       created: Long,
                       deleted: Long,
                       batches: Long,
                       @JsonProperty("version_conflicts") private val version_conflicts: Long,
                       noops: Long,
                       retries : Retries,
                       @JsonProperty("throttled_millis") private val throttled_millis: Long
                     ) {
  def versionConflicts: Long = version_conflicts

  def throttledTime: FiniteDuration = throttled_millis.millis
}

case class Retries(bulk: Long, search: Long)
