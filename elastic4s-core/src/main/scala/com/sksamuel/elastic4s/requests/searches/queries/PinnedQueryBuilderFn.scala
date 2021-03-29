package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object PinnedQueryBuilderFn {
  def apply(q: PinnedQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("pinned")
    builder.array("ids", q.ids)
    builder.rawField("organic", QueryBuilderFn(q.organic))
    builder.endObject()
    builder
  }
}
