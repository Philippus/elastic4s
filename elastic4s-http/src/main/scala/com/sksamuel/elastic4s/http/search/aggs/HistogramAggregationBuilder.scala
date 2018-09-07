package com.sksamuel.elastic4s.http.search.aggs

import org.elasticsearch.common.xcontent.{ XContentBuilder, XContentFactory }

import com.sksamuel.elastic4s.searches.aggs.HistogramAggregation

object HistogramAggregationBuilder {
  def apply(agg: HistogramAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("histogram")
    agg.field.foreach(builder.field("field", _))
    agg.format.foreach(builder.field("format", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.interval.foreach(builder.field("interval", _))
    agg.keyed.foreach(builder.field("keyed", _))
    agg.offset.foreach(builder.field("offset", _))
    agg.order.foreach(builder.field("order", _))
    agg.script.foreach(builder.field("script", _))

    agg.extendedBounds.foreach{ case (min, max) =>
      builder.startObject("extended_bounds")
      builder.field("min", min)
      builder.field("max", max)
      builder.endObject()
    }

    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
