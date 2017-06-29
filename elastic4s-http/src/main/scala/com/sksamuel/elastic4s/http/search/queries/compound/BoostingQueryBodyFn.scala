package com.sksamuel.elastic4s.http.search.queries.compound

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.BoostingQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object BoostingQueryBodyFn {
  def apply(q: BoostingQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("boosting")
    builder.rawField("positive", QueryBuilderFn(q.positiveQuery).bytes(), XContentType.JSON)
    builder.rawField("negative", QueryBuilderFn(q.negativeQuery).bytes(), XContentType.JSON)
    q.negativeBoost.foreach(builder.field("negative_boost", _))
    builder.endObject()
    builder.endObject()
  }
}
