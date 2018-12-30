package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.TypeQuery
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object TypeQueryBodyFn {
  def apply(q: TypeQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("type")
    builder.field("value", q.`type`)
    builder.endObject().endObject()
  }
}
