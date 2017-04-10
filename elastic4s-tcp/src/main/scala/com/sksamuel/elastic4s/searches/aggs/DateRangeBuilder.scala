package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeAggregationBuilder

object DateRangeBuilder {

  import scala.collection.JavaConverters._

  def apply(agg: DateRangeAggregation): DateRangeAggregationBuilder = {
    val builder = AggregationBuilders.dateRange(agg.name)
    agg.missing.foreach(builder.missing)
    agg.field.foreach(builder.field)
    agg.format.foreach(builder.format)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.keyed(builder.keyed)
    agg.timeZone(builder.timeZone)

    agg.unboundedFromRanges.foreach {
      case (Some(key), str: String) => builder.addUnboundedFrom(key, str)
      case (Some(key), double: Double) => builder.addUnboundedFrom(key, double)
      case (Some(key), long: Long) => builder.addUnboundedFrom(key, long)
      case (Some(key), int: Int) => builder.addUnboundedFrom(key, int)
      case (Some(key), float: Float) => builder.addUnboundedFrom(key, float)
      case (None, str: String) => builder.addUnboundedFrom(str)
      case (None, double: Double) => builder.addUnboundedFrom(double)
      case (None, long: Long) => builder.addUnboundedFrom(long)
      case (None, int: Int) => builder.addUnboundedFrom(int)
      case (None, float: Float) => builder.addUnboundedFrom(float)
    }

    agg.unboundedToRanges.foreach {
      case (Some(key), str: String) => builder.addUnboundedTo(key, str)
      case (Some(key), double: Double) => builder.addUnboundedTo(key, double)
      case (Some(key), long: Long) => builder.addUnboundedTo(key, long)
      case (Some(key), int: Int) => builder.addUnboundedTo(key, int)
      case (Some(key), float: Float) => builder.addUnboundedTo(key, float)
      case (None, str: String) => builder.addUnboundedTo(str)
      case (None, double: Double) => builder.addUnboundedTo(double)
      case (None, long: Long) => builder.addUnboundedTo(long)
      case (None, int: Int) => builder.addUnboundedTo(int)
      case (None, float: Float) => builder.addUnboundedTo(float)
    }

    agg.ranges.foreach {
      case (Some(key), from: String, to: String) => builder.addRange(key, from, to)
      case (Some(key), from: Double, to: Double) => builder.addRange(key, from, to)
      case (Some(key), from: Long, to: Long) => builder.addRange(key, from, to)
      case (Some(key), from: Int, to: Int) => builder.addRange(key, from, to)
      case (Some(key), from: Float, to: Float) => builder.addRange(key, from, to)
      case (None, from: String, to: String) => builder.addRange(from, to)
      case (None, from: Double, to: Double) => builder.addRange(from, to)
      case (None, from: Long, to: Long) => builder.addRange(from, to)
      case (None, from: Int, to: Int) => builder.addRange(from, to)
      case (None, from: Float, to: Float) => builder.addRange(from, to)
    }


    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
