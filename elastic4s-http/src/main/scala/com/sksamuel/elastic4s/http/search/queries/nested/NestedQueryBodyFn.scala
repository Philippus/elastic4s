package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.NestedQueryDefinition

object NestedQueryBodyFn {
  def apply(q: NestedQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("nested")
    builder.field("path", q.path)
    q.scoreMode.map(_.name.toLowerCase).foreach(builder.field("score_mode", _))
    builder.rawField("query", QueryBuilderFn(q.query))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    q.inner.foreach(inner => builder.rawField("inner_hits", InnerHitQueryBodyFn(inner)))
    builder.endObject()
    builder.endObject()
  }
}
