package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.script
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  CompositeAggregation,
  DateHistogramValueSource,
  GeoTileGridValueSource,
  HistogramValueSource,
  SubAggsBuilderFn
}

object CompositeAggregationBuilder {
  def apply(
      agg: CompositeAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("composite")
    agg.size.foreach(builder.field("size", _))
    builder.startArray("sources")
    agg.sources.foreach(s => {
      builder.startObject()
      builder.startObject(s.name)
      builder.startObject(s.valueSourceType)
      s.field.foreach(builder.field("field", _))
      s.script.foreach(s => builder.rawField("script", script.ScriptBuilderFn(s)))
      s.order.foreach(builder.field("order", _))
      if (s.missingBucket) builder.field("missing_bucket", true)
      s match {
        case HistogramValueSource(_, interval, _, _, _, _)                                           => builder.field("interval", interval)
        case DateHistogramValueSource(_, calendarInterval, None, None, _, _, _, timeZone, format, _) =>
          builder.field("calendar_interval", calendarInterval.get)
          timeZone.foreach(builder.field("time_zone", _))
          format.foreach(builder.field("format", _))
        case DateHistogramValueSource(_, None, fixedInterval, None, _, _, _, timeZone, format, _)    =>
          builder.field("fixed_interval", fixedInterval.get)
          timeZone.foreach(builder.field("time_zone", _))
          format.foreach(builder.field("format", _))
        case DateHistogramValueSource(_, None, None, interval, _, _, _, timeZone, format, _)         =>
          builder.field("interval", interval.get)
          timeZone.foreach(builder.field("time_zone", _))
          format.foreach(builder.field("format", _))
        case GeoTileGridValueSource(_, precision, bounds, _, _, _, _)                                =>
          precision.foreach(builder.field("precision", _))
          bounds.foreach(bound => {
            builder.startObject("bounds")

            builder.startArray("top_left")
            builder.value(bound.topLeft.long)
            builder.value(bound.topLeft.lat)
            builder.endArray()

            builder.startArray("bottom_right")
            builder.value(bound.bottomRight.long)
            builder.value(bound.bottomRight.lat)
            builder.endArray()

            builder.endObject()
          })
        case _                                                                                       =>
      }
      builder.endObject()
      builder.endObject()
      builder.endObject()
    })
    builder.endArray()
    agg.after.map(afterMap => {
      builder.startObject("after")
      afterMap.map { case (k, v) => builder.autofield(k, v) }
      builder.endObject()
    })
    builder.endObject()
    SubAggsBuilderFn(agg, builder, customAggregations)
    builder
  }
}
