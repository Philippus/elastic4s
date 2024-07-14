package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.{EnumConversions, handlers}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AggMetaDataFn, DateHistogramAggregation, ExtendedBoundsBuilderFn, SubAggsBuilderFn}

object DateHistogramAggregationBuilder {
  def apply(agg: DateHistogramAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("date_histogram")

    // only one of these three options is allowed in an aggregation
    agg.fixedInterval.map(EnumConversions.interval).foreach(builder.field("fixed_interval", _))
    agg.calendarInterval.map(EnumConversions.interval).foreach(builder.field("calendar_interval", _))

    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.timeZone.map(EnumConversions.timeZone).foreach(builder.field("time_zone", _))
    agg.order.map(EnumConversions.order).foreach(builder.rawField("order", _))
    agg.offset.foreach(builder.field("offset", _))
    agg.format.foreach(builder.field("format", _))
    agg.keyed.foreach(builder.field("keyed", _))
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    agg.extendedBounds.foreach { bounds =>
      builder.rawField("extended_bounds", ExtendedBoundsBuilderFn(bounds))
    }
    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
