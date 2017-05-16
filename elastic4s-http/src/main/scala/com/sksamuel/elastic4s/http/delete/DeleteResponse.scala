package com.sksamuel.elastic4s.http.delete

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.DocumentRef
import com.sksamuel.elastic4s.http.values.Shards

case class DeleteResponse(@JsonProperty("_shards") shards: Shards,
                          found: Boolean,
                          @JsonProperty("_index") index: String,
                          @JsonProperty("_type") `type`: String,
                          @JsonProperty("_id") id: String,
                          @JsonProperty("_version") version: Long,
                          result: String) {
  def ref = DocumentRef(index, `type`, id)
}
