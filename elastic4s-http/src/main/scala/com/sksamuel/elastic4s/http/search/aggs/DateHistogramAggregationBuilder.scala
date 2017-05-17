package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.{EnumConversions, ScriptBuilderFn}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.DateHistogramAggregation

object DateHistogramAggregationBuilder {
  def apply(agg: DateHistogramAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("date_histogram")

    agg.interval.map(EnumConversions.interval).foreach(builder.field("interval", _))
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.timeZone.map(EnumConversions.timeZone).foreach(builder.field("time_zone", _))
    agg.order.map(EnumConversions.order).foreach(builder.rawField("order", _))
    agg.offset.foreach(builder.field("offset", _))
    agg.format.foreach(builder.field("format", _))
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    agg.extendedBounds.foreach(bounds => ???) ///builder.field("extended_bounds", ???))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
