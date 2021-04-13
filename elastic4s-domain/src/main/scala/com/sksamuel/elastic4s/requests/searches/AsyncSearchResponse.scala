package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty

case class AsyncSearchResponse(
                                id: String,
                                @JsonProperty("is_partial") isPartial: Boolean,
                                @JsonProperty("is_running") isRunning: Boolean,
                                @JsonProperty("start_time_in_millis") startTime: Long,
                                @JsonProperty("expiration_time_in_millis") expirationTime: Long,
                                response: SearchResponse
                              )
