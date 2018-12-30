package com.sksamuel.elastic4s.requests.searches.queries.text

import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchPhrase
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MatchPhraseQueryBodyFn {
  def apply(q: MatchPhrase): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_phrase")
    builder.startObject(q.field)
    builder.autofield("query", q.value)
    q.analyzer.foreach(builder.field("analyzer", _))
    q.slop.foreach(builder.field("slop", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
