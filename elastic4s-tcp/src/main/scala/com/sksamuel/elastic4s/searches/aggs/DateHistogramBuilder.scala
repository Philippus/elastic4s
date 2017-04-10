package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder

import scala.collection.JavaConverters._

object DateHistogramBuilder {
  def apply(agg: DateHistogramAggregation): DateHistogramAggregationBuilder = {
    val builder = AggregationBuilders.dateHistogram(agg.name)
    agg.extendedBounds.foreach(builder.extendedBounds)
    agg.field.foreach(builder.field)
    agg.format.foreach(builder.format)
    agg.interval.foreach(builder.dateHistogramInterval)
    agg.minDocCount.foreach(builder.minDocCount)
    agg.missing.foreach(builder.missing)
    agg.offset.foreach(builder.offset)
    agg.order.foreach(builder.order)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.timeZone.foreach(builder.timeZone)
    SubAggsFn(builder, agg.subaggs)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
