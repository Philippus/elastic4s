package com.sksamuel.elastic4s.requests.searches.queries.compound

import com.sksamuel.elastic4s.requests.searches.queries.{ConstantScore, QueryBuilderFn}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ConstantScoreBodyFn {
  def apply(q: ConstantScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("constant_score")
    builder.rawField("filter", QueryBuilderFn(q.query))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
