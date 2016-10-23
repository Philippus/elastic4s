package com.sksamuel.elastic4s.search

case class MultiSearchResult(original: MultiSearchResponse) {
  def size = items.size
  def items: Seq[MultiSearchResultItem] = original.getResponses.map(MultiSearchResultItem.apply)
  // backwards compat
  def getResponses(): Array[MultiSearchResponse.Item] = original.getResponses
}
