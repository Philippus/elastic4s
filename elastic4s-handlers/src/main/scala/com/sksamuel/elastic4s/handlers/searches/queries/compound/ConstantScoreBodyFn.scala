package com.sksamuel.elastic4s.handlers.searches.queries.compound

import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.ConstantScore

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
