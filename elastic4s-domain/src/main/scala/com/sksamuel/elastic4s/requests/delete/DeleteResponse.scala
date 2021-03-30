package com.sksamuel.elastic4s.requests.delete

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.{DocumentRef, Shards}

case class DeleteResponse(@JsonProperty("_shards") shards: Shards,
                          @JsonProperty("_index") index: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          result: String) {
  def ref: DocumentRef = DocumentRef(index, id)
}
