package com.sksamuel.elastic4s.requests.delete

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.{DocumentRef, Shards}

case class DeleteResponse(@JsonProperty("_shards") shards: Shards,
                          @JsonProperty("_index") index: String,
                          @JsonProperty("_type") `type`: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          result: String) {
  @deprecated(
    "this is no longer included in the json result from elasticsearch, use result which has values of not_found and deleted",
    "6.0.0"
  )
  def found: Boolean = result == "deleted"
  def ref            = DocumentRef(index, `type`, id)
}
