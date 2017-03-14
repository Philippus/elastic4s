package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.searches.queries.ExistsQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ExistsQueryBodyFn {
  def apply(q: ExistsQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("exists")
    builder.field("field", q.field)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
