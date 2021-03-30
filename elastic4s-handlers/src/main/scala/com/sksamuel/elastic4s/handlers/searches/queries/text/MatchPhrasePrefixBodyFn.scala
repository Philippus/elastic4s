package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchPhrasePrefixQuery

object MatchPhrasePrefixBodyFn {
  def apply(q: MatchPhrasePrefixQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_phrase_prefix")
    builder.startObject(q.field)
    builder.autofield("query", q.value)
    q.queryName.foreach(builder.field("_name", _))
    q.analyzer.foreach(builder.field("analyzer", _))
    q.slop.foreach(builder.field("slop", _))
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.boost.foreach(builder.field("boost", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
