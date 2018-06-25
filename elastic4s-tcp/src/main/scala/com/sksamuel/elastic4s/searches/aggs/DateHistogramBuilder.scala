package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.{EnumConversions, ScriptBuilder}
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{
  DateHistogramAggregationBuilder,
  DateHistogramInterval,
  ExtendedBounds => ESExtendedBounds
}

import scala.collection.JavaConverters._

object DateHistogramBuilder {
  def apply(agg: DateHistogramAggregation): DateHistogramAggregationBuilder = {
    val builder = AggregationBuilders.dateHistogram(agg.name)
    agg.extendedBounds.foreach {
      case LongExtendedBounds(min, max)   => builder.extendedBounds(new ESExtendedBounds(min, max))
      case DoubleExtendedBounds(min, max) => builder.extendedBounds(new ESExtendedBounds(min.toLong, max.toLong))
      case StringExtendedBounds(min, max) => builder.extendedBounds(new ESExtendedBounds(min, max))
      case DateExtendedBounds(min, max)   => builder.extendedBounds(new ESExtendedBounds(min.show, max.show))
    }
    agg.field.foreach(builder.field)
    agg.format.foreach(builder.format)
    agg.interval.map(_.interval).map(new DateHistogramInterval(_)).foreach(builder.dateHistogramInterval)
    agg.minDocCount.foreach(builder.minDocCount)
    agg.missing.foreach(builder.missing)
    agg.offset.foreach(builder.offset)
    agg.keyed.foreach(builder.keyed)
    agg.order.map(EnumConversions.histogramOrder).foreach(builder.order)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.timeZone.foreach(builder.timeZone)
    SubAggsFn(builder, agg.subaggs)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
