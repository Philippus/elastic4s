package com.sksamuel.elastic4s.handlers.explain

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.explain.ExplainRequest

object ExplainBodyFn {
  def apply(v: ExplainRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", queries.QueryBuilderFn(v.query.get))
    builder.endObject()
  }
}
