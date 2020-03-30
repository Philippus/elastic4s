package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.TypeQuery

object TypeQueryBodyFn {
  def apply(q: TypeQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("type")
    builder.field("value", q.`type`)
    builder.endObject().endObject()
  }
}
