package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.span.{SpanNotQueryDefinition, SpanOrQueryDefinition}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SpanOrQueryBodyFn {
  def apply(q: SpanOrQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.startObject()
    builder.startObject("span_or")
    builder.startArray("clauses")
    q.clauses.foreach { clause =>
      builder.rawValue(QueryBuilderFn(clause).bytes)
    }
    builder.endArray()

    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}


