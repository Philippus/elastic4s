package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.CardinalityAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object CardinalityAggregationBuilder {
  def apply(agg: CardinalityAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject().startObject("cardinality")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.precisionThreshold.foreach(builder.field("precision_threshold", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes)
    }
    builder.endObject().endObject()
  }
}
