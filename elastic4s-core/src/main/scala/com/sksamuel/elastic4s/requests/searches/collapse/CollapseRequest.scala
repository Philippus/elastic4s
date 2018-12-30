package com.sksamuel.elastic4s.requests.searches.collapse

import com.sksamuel.elastic4s.requests.searches.queries.InnerHit
import com.sksamuel.exts.OptionImplicits._

case class CollapseRequest(field: String,
                           inner: Option[InnerHit] = None,
                           maxConcurrentGroupSearches: Option[Int] = None) {

  def inner(inner: InnerHit): CollapseRequest               = copy(inner = inner.some)
  def maxConcurrentGroupSearches(max: Int): CollapseRequest = copy(maxConcurrentGroupSearches = max.some)
}
