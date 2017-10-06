package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.http.search.queries.span.XContentBuilderExtensions._
import com.sksamuel.elastic4s.searches.queries.span.SpanOrQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SpanOrQueryBodyFn {
  def apply(q: SpanOrQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.startObject()
    builder.startObject("span_or")
    builder.startArray("clauses")
    builder.rawArrayValue(q.clauses.map(QueryBuilderFn.apply))
    builder.endArray()

    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}


