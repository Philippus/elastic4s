package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.{EnumConversions, handlers}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  AggMetaDataFn,
  ExtendedBoundsBuilderFn,
  HistogramAggregation,
  SubAggsBuilderFn
}

object HistogramAggregationBuilder {
  def apply(
      agg: HistogramAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {

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
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    agg.extendedBounds.foreach { bounds =>
      builder.rawField("extended_bounds", ExtendedBoundsBuilderFn(bounds))
    }
    agg.hardBounds.foreach { bounds =>
      builder.rawField("hard_bounds", ExtendedBoundsBuilderFn(bounds))
    }
    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
