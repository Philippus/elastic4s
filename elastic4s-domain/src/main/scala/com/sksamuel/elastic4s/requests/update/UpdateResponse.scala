package com.sksamuel.elastic4s.requests.update

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.{DocumentRef, Shards}

case class UpdateResponse(
    @JsonProperty("_index") index: String,
    @JsonProperty("_id") id: String,
    @JsonProperty("_version") version: Long,
    @JsonProperty("_seq_no") seqNo: Long,
    @JsonProperty("_primary_term") primaryTerm: Long,
    result: String,
    @JsonProperty("forcedRefresh") forcedRefresh: Boolean,
    @JsonProperty("_shards") shards: Shards,
    private val get: Option[UpdateGet]
) {
  def ref: DocumentRef         = DocumentRef(index, id)
  def source: Map[String, Any] = get.flatMap(get => Option(get._source)).getOrElse(Map.empty)
  def found: Boolean           = get.forall(_.found)
}
