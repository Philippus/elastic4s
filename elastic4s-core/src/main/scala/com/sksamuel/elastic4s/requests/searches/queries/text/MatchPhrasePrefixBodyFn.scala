package com.sksamuel.elastic4s.requests.searches.queries.text

import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchPhrasePrefix
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MatchPhrasePrefixBodyFn {
  def apply(q: MatchPhrasePrefix): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_phrase_prefix")
    builder.startObject(q.field)
    builder.autofield("query", q.value)
    q.analyzer.foreach(builder.field("analyzer", _))
    q.slop.foreach(builder.field("slop", _))
    q.maxExpansions.foreach(builder.field("max_expansions", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder.endObject()
    builder
  }
}
