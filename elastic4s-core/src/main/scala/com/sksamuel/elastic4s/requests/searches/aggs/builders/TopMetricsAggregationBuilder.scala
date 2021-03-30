package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.searches.queries.sort.SortBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.TopMetricsAggregation

object TopMetricsAggregationBuilder {

  def apply(agg: TopMetricsAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("top_metrics")

    agg.size.foreach(builder.field("size", _))

    if (agg.metrics.size == 1) {
      builder.startObject("metrics")
      builder.field("field", agg.metrics.head)
      builder.endObject()
    } else {
      builder.startArray("metrics")
      agg.metrics.foreach { metric =>
        builder.startObject()
        builder.field("field", metric)
        builder.endObject()
      }
      builder.endArray()
    }

    agg.sort.foreach { sort =>
      builder.rawField("sort", SortBuilderFn(sort))
    }

    builder.endObject().endObject()
  }
}
