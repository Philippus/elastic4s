package com.sksamuel.elastic4s.handlers.searches.queries.compound

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.BoostingQuery

object BoostingQueryBodyFn {
  def apply(q: BoostingQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("boosting")
    builder.rawField("positive", QueryBuilderFn(q.positiveQuery))
    builder.rawField("negative", queries.QueryBuilderFn(q.negativeQuery))
    builder.field("negative_boost", q.negativeBoost)
    builder.endObject()
    builder.endObject()
  }
}
