package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty

import scala.concurrent.duration.{DurationLong, FiniteDuration}

case class TaskResponse(
    took: Option[Long] = None,
    @JsonProperty("timed_out") timedOut: Option[Boolean] = None,
    total: Option[Long] = None,
    updated: Option[Long] = None,
    created: Option[Long] = None,
    deleted: Option[Long] = None,
    batches: Option[Long] = None,
    @JsonProperty("version_conflicts") versionConflicts: Option[Long] = None,
    noops: Option[Long] = None,
    retries: Option[Retries] = None,
    throttled: Option[String] = None,
    @JsonProperty("throttled_millis") throttledMillis: Option[Long] = None,
    @JsonProperty("requests_per_second") requestsPerSecond: Option[Float] = None,
    @JsonProperty("throttled_until") throttledUntil: Option[String] = None,
    @JsonProperty("throttled_until_millis") throttledUntilMillis: Option[Long] = None,
    task: Option[String] = None,
    failures: Option[Seq[TaskFailure]] = None
) {
  def throttledTime: Option[FiniteDuration]      = throttledMillis.map(_.millis)
  def throttledUntilTime: Option[FiniteDuration] = throttledUntilMillis.map(_.millis)
}
