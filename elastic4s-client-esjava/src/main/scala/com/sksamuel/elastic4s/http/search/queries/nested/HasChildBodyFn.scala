package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.HasChildQuery

object HasChildBodyFn {

  def apply(q: HasChildQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("has_child")
    builder.field("type", q.`type`)
    q.minChildren.foreach(builder.field("min_children", _))
    q.maxChildren.foreach(builder.field("max_children", _))
    builder.field("score_mode", EnumConversions.scoreMode(q.scoreMode))
    builder.rawField("query", QueryBuilderFn(q.query))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    q.innerHit.foreach(inner => builder.rawField("inner_hits", InnerHitQueryBodyFn(inner)))
    builder.endObject()
    builder.endObject()
    builder
  }
}
