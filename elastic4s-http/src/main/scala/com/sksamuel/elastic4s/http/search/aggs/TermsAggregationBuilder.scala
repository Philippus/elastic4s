package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.{EnumConversions, ScriptBuilderFn}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.TermsAggregationDefinition

object TermsAggregationBuilder {
  def apply(agg: TermsAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("terms")

    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.executionHint.foreach(builder.field("execution_hint", _))
    agg.collectMode.map(EnumConversions.collectMode).foreach(builder.field("collect_mode", _))
    agg.size.foreach(builder.field("size", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    agg.includeExclude.foreach { inex =>
      if (inex.include.nonEmpty)
        builder.array("include", inex.include.toArray)
      if (inex.include.nonEmpty)
        builder.array("exclude", inex.include.toArray)
    }
    agg.includePartition.foreach { incpart =>
      builder.field("partition", incpart.partition)
      builder.field("num_partitions", incpart.numPartitions)
    }
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.shardMinDocCount.foreach(builder.field("shard_min_doc_count", _))
    agg.shardSize.foreach(builder.field("shard_size", _))
    agg.showTermDocCountError.foreach(builder.field("show_term_doc_count_error", _))
    agg.order.map(EnumConversions.order).foreach(builder.rawField("order", _))

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}


