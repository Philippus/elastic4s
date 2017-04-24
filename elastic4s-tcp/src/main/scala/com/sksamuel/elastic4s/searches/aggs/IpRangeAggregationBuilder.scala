package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.ip.IpRangeAggregationBuilder

import scala.collection.JavaConverters._

object IpRangeAggregationBuilder {

  def apply(agg: IpRangeAggregationDefinition): IpRangeAggregationBuilder = {

    val builder = AggregationBuilders.ipRange(agg.name)

    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)

    agg.maskRanges.foreach {
      case (Some(key), mask) => builder.addMaskRange(key, mask)
      case (None, mask) => builder.addMaskRange(mask)
    }

    agg.ranges.foreach {
      case (Some(key), from, to) => builder.addRange(key, from, to)
      case (None, from, to) => builder.addRange(from, to)
    }

    agg.unboundedFrom.foreach {
      case (Some(key), from) => builder.addUnboundedFrom(key, from)
      case (None, from) => builder.addUnboundedFrom(from)
    }

    agg.unboundedTo.foreach {
      case (Some(key), to) => builder.addUnboundedTo(key, to)
      case (None, to) => builder.addUnboundedTo(to)
    }

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
