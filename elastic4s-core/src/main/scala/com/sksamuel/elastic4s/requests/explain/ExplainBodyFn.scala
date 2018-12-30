package com.sksamuel.elastic4s.requests.explain

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ExplainBodyFn {
  def apply(v: ExplainRequest): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("query", QueryBuilderFn(v.query.get))
    builder.endObject()
  }
}
