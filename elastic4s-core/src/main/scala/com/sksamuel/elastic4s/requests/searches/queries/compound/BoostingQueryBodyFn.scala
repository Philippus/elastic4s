package com.sksamuel.elastic4s.requests.searches.queries.compound

import com.sksamuel.elastic4s.requests.searches.queries.{BoostingQuery, QueryBuilderFn}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object BoostingQueryBodyFn {
  def apply(q: BoostingQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("boosting")
    builder.rawField("positive", QueryBuilderFn(q.positiveQuery))
    builder.rawField("negative", QueryBuilderFn(q.negativeQuery))
    q.negativeBoost.foreach(builder.field("negative_boost", _))
    q.boost.foreach(builder.field("boost", _))
    builder.endObject()
    builder.endObject()
  }
}
