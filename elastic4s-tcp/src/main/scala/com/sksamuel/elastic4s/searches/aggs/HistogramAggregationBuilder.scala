package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder

import scala.collection.JavaConverters._

object HistogramAggregationBuilder {

  def apply(agg: HistogramAggregation): HistogramAggregationBuilder = {
    val builder = AggregationBuilders.histogram(agg.name)
    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.order.foreach(builder.order)
    agg.keyed.foreach(builder.keyed)
    agg.interval.foreach(builder.interval)
    agg.minDocCount.foreach(builder.minDocCount)
    agg.offset.foreach(builder.offset)
    agg.extendedBounds.foreach { case (min, max) => builder.extendedBounds(min, max) }
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
