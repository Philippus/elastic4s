package com.sksamuel.elastic4s.requests.indexes

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.DocumentRef
import com.sksamuel.elastic4s.requests.common.Preference.Shards

case class IndexResponse(@JsonProperty("_id") id: String,
                         @JsonProperty("_index") index: String,
                         @JsonProperty("_type") `type`: String,
                         @JsonProperty("_version") version: Long,
                         result: String,
                         @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                         shards: Shards) {
  def ref = DocumentRef(index, `type`, id)
}
