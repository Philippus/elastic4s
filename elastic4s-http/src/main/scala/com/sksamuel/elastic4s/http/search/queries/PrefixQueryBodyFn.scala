package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.PrefixQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object PrefixQueryBodyFn {
  def apply(q: PrefixQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("prefix")
    builder.startObject(q.field)
    builder.field("value", q.prefix)
    q.rewrite.foreach(builder.field("rewrite", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
