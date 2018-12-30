package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.exts.OptionImplicits._

case class MultiSearchRequest(searches: Iterable[SearchRequest], maxConcurrentSearches: Option[Int] = None, typedKeys: Option[Boolean] = None) {
  def maxConcurrentSearches(max: Int): MultiSearchRequest = copy(maxConcurrentSearches = max.some)

  def typedKeys(enabled: Boolean): MultiSearchRequest = copy(typedKeys = enabled.some)
}
