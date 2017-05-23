package com.sksamuel.elastic4s.http.search.queries.specialized

import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object WeightScoreBodyFn {
  def apply(weight: Double): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.field("weight", weight.toFloat)
    builder.endObject()
    builder
  }
}
