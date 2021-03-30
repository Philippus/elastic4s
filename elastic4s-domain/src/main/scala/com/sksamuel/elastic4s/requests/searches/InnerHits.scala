package com.sksamuel.elastic4s.requests.searches

import com.fasterxml.jackson.annotation.JsonProperty

case class InnerHits(total: Total,
                     @JsonProperty("max_score") maxScore: Option[Double],
                     hits: Seq[InnerHit]) {
  def size: Long = hits.length
  def isEmpty: Boolean = hits.isEmpty
  def nonEmpty: Boolean = hits.nonEmpty
}
