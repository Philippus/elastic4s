package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchAllQuery, MatchNoneQuery}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

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
