package com.sksamuel.elastic4s.searches

import org.elasticsearch.action.search.ClearScrollResponse

case class ClearScrollResult(response: ClearScrollResponse) {
  def number: Int = response.getNumFreed
  def success: Boolean = response.isSucceeded
}
