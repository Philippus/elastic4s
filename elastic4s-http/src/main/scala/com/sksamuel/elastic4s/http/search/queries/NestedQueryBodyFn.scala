package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.NestedQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object NestedQueryBodyFn {
  def apply(q: NestedQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("nested")
    builder.field("path", q.path)
    q.scoreMode.map(_.name.toLowerCase).foreach(builder.field("score_mode", _))
    builder.rawField("query", QueryBuilderFn(q.query).bytes)
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
