package com.sksamuel.elastic4s.searches

import com.sksamuel.exts.OptionImplicits._

case class MultiSearchRequest(searches: Iterable[SearchRequest], maxConcurrentSearches: Option[Int] = None) {
  def maxConcurrentSearches(max: Int): MultiSearchRequest = copy(maxConcurrentSearches = max.some)
}
