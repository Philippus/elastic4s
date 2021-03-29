package com.sksamuel.elastic4s.requests.explain

import com.fasterxml.jackson.annotation.JsonProperty
import com.sksamuel.elastic4s.requests.common.DocumentRef

case class Explanation(value: Double, description: String, details: Seq[Explanation])

case class ExplainResponse(@JsonProperty("_index") index: String,
                           @JsonProperty("_type") `type`: String,
                           @JsonProperty("_id") id: String,
                           matched: Boolean,
                           explanation: Explanation) {

  def isMatch: Boolean = matched
  def ref: DocumentRef = DocumentRef(index, `type`, id)
}
