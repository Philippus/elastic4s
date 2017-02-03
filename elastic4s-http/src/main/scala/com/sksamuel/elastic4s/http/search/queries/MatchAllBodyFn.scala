package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.matches.MatchAllQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object MatchAllBodyFn {
  def apply(q: MatchAllQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("match_all")
    q.boost.foreach(builder.field("boost", _))
    builder.endObject()
    builder.endObject()
  }
}
