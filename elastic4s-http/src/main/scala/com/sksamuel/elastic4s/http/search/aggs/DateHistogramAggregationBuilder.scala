package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.DateHistogramAggregation
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object DateHistogramAggregationBuilder {
  def apply(agg: DateHistogramAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("date_histogram")
    agg.interval.foreach(builder.field("interval", _))
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.timeZone.foreach(builder.field("time_zone", _))
    agg.order.foreach(builder.field("order", _))
    agg.offset.foreach(builder.field("offset", _))
    agg.format.foreach(builder.field("format", _))
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes)
    }
    agg.missing.foreach(builder.field("missing", _))
    agg.extendedBounds.foreach(builder.field("extended_bounds", _))
    builder.endObject()    
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}