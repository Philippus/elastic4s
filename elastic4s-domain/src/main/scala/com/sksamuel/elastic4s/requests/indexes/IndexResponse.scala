package com.sksamuel.elastic4s.requests.indexes

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.{DocumentRef, Shards}

case class IndexResponse(@JsonProperty("_id") id: String,
                         @JsonProperty("_index") index: String,
                         @JsonProperty("_type") `type`: String,
                         @JsonProperty("_version") version: Long,
                         @JsonProperty("_seq_no") seqNo: Long,
                         @JsonProperty("_primary_term") primaryTerm: Long,
                         result: String,
                         @JsonProperty("forced_refresh") forcedRefresh: Boolean,
                         @JsonProperty("_shards") shards: Shards) {
  def ref = DocumentRef(index, `type`, id)
}
