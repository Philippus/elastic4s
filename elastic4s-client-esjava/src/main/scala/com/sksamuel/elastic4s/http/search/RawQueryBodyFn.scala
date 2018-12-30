package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.RawQuery

object RawQueryBodyFn {
  def apply(q: RawQuery): XContentBuilder = XContentFactory.parse(q.json)
}
