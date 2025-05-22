package com.sksamuel.elastic4s.handlers.searches.queries.text

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchPhraseQuery

object MatchPhraseQueryBodyFn {
  def apply(q: MatchPhraseQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_phrase")
    builder.startObject(q.field)
    builder.autofield("query", q.value)
    q.queryName.foreach(builder.field("_name", _))
    q.analyzer.foreach(builder.field("analyzer", _))
    q.slop.foreach(builder.field("slop", _))
    q.boost.foreach(builder.field("boost", _))
    q.zeroTermsQuery.map(EnumConversions.zeroTermsQuery).foreach(builder.field("zero_terms_query", _))
    builder.endObject().endObject().endObject()
  }
}
