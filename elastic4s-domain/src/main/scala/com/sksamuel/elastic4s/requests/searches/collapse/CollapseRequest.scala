package com.sksamuel.elastic4s.requests.searches.collapse

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.queries.InnerHit

case class CollapseRequest(
    field: String,
    inner: Option[InnerHit] = None,
    maxConcurrentGroupSearches: Option[Int] = None
) {

  def inner(inner: InnerHit): CollapseRequest               = copy(inner = inner.some)
  def maxConcurrentGroupSearches(max: Int): CollapseRequest = copy(maxConcurrentGroupSearches = max.some)
}
