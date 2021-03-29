package com.sksamuel.elastic4s.requests.delete

import com.fasterxml.jackson.annotation.JsonProperty

case class DeleteByQueryResponse(took: Long,
                                 @JsonProperty("timed_out") timedOut: Boolean,
                                 total: Long,
                                 deleted: Long,
                                 batches: Long,
                                 @JsonProperty("version_conflicts") versionConflicts: Long,
                                 noops: Long,
                                 @JsonProperty("throttled_millis") throttledMillis: Long,
                                 @JsonProperty("requests_per_second") requestsPerSecond: Long,
                                 @JsonProperty("throttled_until_millis") throttledUntilMillis: Long)
