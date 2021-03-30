package com.sksamuel.elastic4s.handlers.searches

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.RawQuery

object RawQueryBodyFn {
  def apply(q: RawQuery): XContentBuilder = XContentFactory.parse(q.json)
}
