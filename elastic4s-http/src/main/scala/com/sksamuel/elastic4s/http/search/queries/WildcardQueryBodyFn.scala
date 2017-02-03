package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.WildcardQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object WildcardQueryBodyFn {
  def apply(q: WildcardQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("wildcard")
    builder.startObject(q.field)
    builder.field("value", q.query)
    q.rewrite.foreach(builder.field("rewrite", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
