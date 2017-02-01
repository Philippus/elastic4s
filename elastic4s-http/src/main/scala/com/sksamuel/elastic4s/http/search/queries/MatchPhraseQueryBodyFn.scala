package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.`match`.MatchPhraseDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object MatchPhraseQueryBodyFn {
  def apply(q: MatchPhraseDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("match_phrase")
    builder.startObject(q.field)
    builder.field("query", q.value)
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
