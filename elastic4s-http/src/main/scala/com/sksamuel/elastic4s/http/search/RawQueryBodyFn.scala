package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.RawQueryDefinition

object RawQueryBodyFn {
  def apply(q: RawQueryDefinition): XContentBuilder = XContentFactory.parse(q.json)
}
