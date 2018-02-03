package com.sksamuel.elastic4s.searches.collapse

import com.sksamuel.elastic4s.searches.queries.InnerHitDefinition
import com.sksamuel.exts.OptionImplicits._

case class CollapseRequest(field: String,
                           inner: Option[InnerHitDefinition] = None,
                           maxConcurrentGroupSearches: Option[Int] = None) {

  def inner(inner: InnerHitDefinition): CollapseRequest     = copy(inner = inner.some)
  def maxConcurrentGroupSearches(max: Int): CollapseRequest = copy(maxConcurrentGroupSearches = max.some)
}
