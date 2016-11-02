package com.sksamuel.elastic4s.search

import org.elasticsearch.action.search.MultiSearchResponse

case class MultiSearchResult(original: MultiSearchResponse) {
  def size = items.size
  def items: Seq[MultiSearchResultItem] = original.getResponses.map(MultiSearchResultItem.apply)
  // backwards compat
  def getResponses(): Array[MultiSearchResponse.Item] = original.getResponses
}
