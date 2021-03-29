package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchNoneQuery

object MatchNoneBodyFn {

  def apply(q: MatchNoneQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_none")
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}
