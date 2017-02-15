package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.BoostingQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object BoostingQueryBodyFn {
  def apply(q: BoostingQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("boosting")
    builder.rawField("positive", QueryBuilderFn(q.positiveQuery).bytes())
    builder.rawField("negative", QueryBuilderFn(q.negativeQuery).bytes())
    q.negativeBoost.foreach(builder.field("negative_boost", _))
    builder.endObject()
    builder.endObject()
  }
}
