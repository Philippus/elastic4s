package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.pipeline.CumulativeSumDefinition
import com.sksamuel.elastic4s.searches.aggs.{SumAggregationDefinition, ValueCountAggregationDefinition}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object CumulativeSumAggregationBuilder {
  def apply(agg: CumulativeSumDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("cumulative_sum")
    builder.field("buckets_path", agg.bucketsPath)
    agg.format.foreach(builder.field("format", _))

    builder.endObject()
    builder.endObject()
  }
}
