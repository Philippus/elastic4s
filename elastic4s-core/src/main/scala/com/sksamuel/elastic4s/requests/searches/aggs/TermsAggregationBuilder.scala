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
    agg.includeExclude.foreach { incexc =>
      incexc.include.toList match {
        case Nil            =>
        case include :: Nil => builder.field("include", include)
        case more           => builder.array("include", more.toArray)
      }
      incexc.exclude.toList match {
        case Nil            =>
        case exclude :: Nil => builder.field("exclude", exclude)
        case more           => builder.array("exclude", more.toArray)
      }
    }
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
