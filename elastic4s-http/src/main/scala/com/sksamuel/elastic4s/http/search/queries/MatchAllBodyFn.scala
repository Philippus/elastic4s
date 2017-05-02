package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.matches.{MatchAllQueryDefinition, MatchNoneQueryDefinition}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object MatchAllBodyFn {
  def apply(q: MatchAllQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject().startObject("match_all")
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}

object MatchNoneBodyFn {

  def apply(q: MatchNoneQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject().startObject("match_none")
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}
