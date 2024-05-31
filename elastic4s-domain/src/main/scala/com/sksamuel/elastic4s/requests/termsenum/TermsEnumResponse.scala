package com.sksamuel.elastic4s.requests.termsenum

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.Shards

case class TermsEnumResponse(terms: Seq[String], complete: Boolean, @JsonProperty("_shards") shards: Shards) {
  def isComplete: Boolean = complete
}
