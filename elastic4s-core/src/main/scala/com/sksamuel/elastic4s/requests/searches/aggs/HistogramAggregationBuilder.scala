package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object HistogramAggregationBuilder {
  def apply(agg: HistogramAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("histogram")

    agg.interval.foreach(builder.field("interval", _))
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.order.map(EnumConversions.order).foreach(builder.rawField("order", _))
    agg.offset.foreach(builder.field("offset", _))
    agg.format.foreach(builder.field("format", _))
    agg.field.foreach(builder.field("field", _))
    agg.keyed.foreach(builder.field("keyed", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    agg.extendedBounds.foreach { bounds =>
      builder.rawField("extended_bounds", ExtendedBoundsBuilderFn(bounds))
    }
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
