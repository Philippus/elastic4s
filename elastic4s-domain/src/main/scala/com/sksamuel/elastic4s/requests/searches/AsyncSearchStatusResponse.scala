package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.Shards

case class AsyncSearchStatusResponse(
                                      id: Option[String],
                                      @JsonProperty("is_partial") isPartial: Boolean,
                                      @JsonProperty("is_running") isRunning: Boolean,
                                      @JsonProperty("start_time_in_millis") startTime: Long,
                                      @JsonProperty("expiration_time_in_millis") expirationTime: Long,
                                      @JsonProperty("_shards") private val _shards: Shards,
                                      @JsonProperty("completion_status") completionStatus: Option[Int]
                                    ) {
  def shards: Shards = Option(_shards).getOrElse(Shards(-1, -1, -1))
}
