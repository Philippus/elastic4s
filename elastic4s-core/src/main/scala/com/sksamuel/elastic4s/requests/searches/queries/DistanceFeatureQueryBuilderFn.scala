package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

object DistanceFeatureQueryBuilderFn {
  def apply(q: DistanceFeatureQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("distance_feature")
    builder.field("field", q.field)
    builder.field("origin", q.origin)
    builder.field("pivot", q.pivot)
    q.boost.foreach(builder.field("boost", _))
    builder.endObject()
    builder
  }
}
