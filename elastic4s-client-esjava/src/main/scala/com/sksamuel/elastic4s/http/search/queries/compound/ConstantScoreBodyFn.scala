package com.sksamuel.elastic4s.http.search.queries.compound

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.ConstantScore

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
