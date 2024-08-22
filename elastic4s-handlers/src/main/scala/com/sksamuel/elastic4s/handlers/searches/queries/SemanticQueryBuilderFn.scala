package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.SemanticQuery

object SemanticQueryBuilderFn {
  def apply(q: SemanticQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("semantic")
    builder.field("field", q.field)
    builder.field("query", q.query)
    builder.endObject()
    builder
  }
}
