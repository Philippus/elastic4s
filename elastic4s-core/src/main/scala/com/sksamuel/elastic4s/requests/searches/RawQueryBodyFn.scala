package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.queries.RawQuery
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object RawQueryBodyFn {
  def apply(q: RawQuery): XContentBuilder = XContentFactory.parse(q.json)
}
