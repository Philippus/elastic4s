package com.sksamuel.elastic4s.requests.validate

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.Shards

case class ValidateResponse(valid: Boolean, @JsonProperty("_shards") shards: Shards, explanations: Seq[Explanation]) {
  def isValid: Boolean = valid
}
