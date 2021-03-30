package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.PinnedQuery

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
