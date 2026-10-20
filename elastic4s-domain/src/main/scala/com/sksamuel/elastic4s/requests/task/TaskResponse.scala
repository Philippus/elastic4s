package com.sksamuel.elastic4s.requests.task

import com.fasterxml.jackson.annotation.JsonProperty
import tools.jackson.databind.annotation.JsonDeserialize

import java.lang.{Float => JFloat, Long => JLong}
import scala.concurrent.duration.{DurationLong, FiniteDuration}

// https://github.com/elastic/elasticsearch-specification/blob/main/specification/_global/reindex/ReindexResponse.ts
// The contentAs annotations are needed because Jackson cannot see through the erased Option[Long]/Option[Float] and
// would otherwise deserialize numbers as java.lang.Integer/Double.
case class TaskResponse(
    @JsonDeserialize(contentAs = classOf[JLong]) took: Option[Long] = None,
    @JsonProperty("timed_out") timedOut: Option[Boolean] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) total: Option[Long] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) updated: Option[Long] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) created: Option[Long] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) deleted: Option[Long] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) batches: Option[Long] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) @JsonProperty("version_conflicts") versionConflicts: Option[Long] =
      None,
    @JsonDeserialize(contentAs = classOf[JLong]) noops: Option[Long] = None,
    retries: Option[Retries] = None,
    throttled: Option[String] = None,
    @JsonDeserialize(contentAs = classOf[JLong]) @JsonProperty("throttled_millis") throttledMillis: Option[Long] = None,
    @JsonDeserialize(contentAs =
      classOf[JFloat]
    ) @JsonProperty("requests_per_second") requestsPerSecond: Option[Float] = None,
    @JsonProperty("throttled_until") throttledUntil: Option[String] = None,
    @JsonDeserialize(contentAs =
      classOf[JLong]
    ) @JsonProperty("throttled_until_millis") throttledUntilMillis: Option[Long] = None,
    task: Option[String] = None,
    failures: Option[Seq[TaskFailure]] = None
) {
  def throttledTime: Option[FiniteDuration]      = throttledMillis.map(_.millis)
  def throttledUntilTime: Option[FiniteDuration] = throttledUntilMillis.map(_.millis)
}
