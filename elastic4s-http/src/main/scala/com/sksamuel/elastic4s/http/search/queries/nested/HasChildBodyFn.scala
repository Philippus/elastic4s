package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.HasChildQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}
import org.apache.lucene.search.join.ScoreMode

object HasChildBodyFn {

  def apply(q: HasChildQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("has_child")
    builder.field("type", q.`type`)
    q.minChildren.foreach(builder.field("min_children", _))
    q.maxChildren.foreach(builder.field("max_children", _))
    builder.field("score_mode", ScoreModeFn(q.scoreMode))
    builder.rawField("query", QueryBuilderFn(q.query).bytes, XContentType.JSON)
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}

object ScoreModeFn {
  def apply(mode: ScoreMode): String = if (mode == ScoreMode.Total) {
    "sum"
  } else {
    mode.name.toLowerCase()
  }
}
