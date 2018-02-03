package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.matches.{MatchAllQuery, MatchNoneQuery}

object MatchAllBodyFn {
  def apply(q: MatchAllQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_all")
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}

object MatchNoneBodyFn {

  def apply(q: MatchNoneQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("match_none")
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}
