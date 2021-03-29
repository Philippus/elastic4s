package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchAllQuery

object MatchAllBodyFn {
  def apply(q: MatchAllQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_all")
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}
