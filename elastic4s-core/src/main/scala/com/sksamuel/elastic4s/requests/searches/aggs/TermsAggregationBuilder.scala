package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object TermsAggregationBuilder {
  def apply(agg: TermsAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("terms")

    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.executionHint.foreach(builder.field("execution_hint", _))
    agg.collectMode.map(EnumConversions.collectMode).foreach(builder.field("collect_mode", _))
    agg.size.foreach(builder.field("size", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }

    if (agg.includeExactValues.nonEmpty)
      builder.array("include", agg.includeExactValues.toArray)
    else
      agg.includeRegex.foreach(builder.field("include", _))

    if (agg.excludeExactValues.nonEmpty)
      builder.array("exclude", agg.excludeExactValues.toArray)
    else
      agg.excludeRegex.foreach(builder.field("exclude", _))

    agg.includePartition.foreach { incpart =>
      val includeBuilder = builder.startObject("include")
      includeBuilder.field("partition", incpart.partition)
      includeBuilder.field("num_partitions", incpart.numPartitions)
      includeBuilder.endObject()
    }
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.shardMinDocCount.foreach(builder.field("shard_min_doc_count", _))
    agg.shardSize.foreach(builder.field("shard_size", _))
    agg.showTermDocCountError.foreach(builder.field("show_term_doc_count_error", _))
    agg.orders match {
      case order if order.isEmpty =>
      case Seq(order)             => builder.rawField("order", EnumConversions.order(order))
      case _ =>
        builder.startArray("order")
        agg.orders.map(EnumConversions.order).foreach(builder.rawValue)
        builder.endArray()
    }

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}
