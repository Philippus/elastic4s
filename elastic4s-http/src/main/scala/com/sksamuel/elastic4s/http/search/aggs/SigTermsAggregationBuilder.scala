package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.SigTermsAggregationDefinition

object SigTermsAggregationBuilder {
  def apply(agg: SigTermsAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("significant_terms")
    agg.shardSize.foreach(builder.field("shard_size", _))
    agg.shardMinDocCount.foreach(builder.field("shard_min_doc_count", _))
    agg.field.foreach(builder.field("field", _))
    agg.minDocCount.foreach(builder.field("min_doc_count", _))
    agg.executionHint.foreach(builder.field("execution_hint", _))
    agg.includeExclude.foreach { incexc =>
      incexc.include.toList match {
        case Nil =>
        case include :: Nil => builder.field("include", include)
        case more => builder.array("include", more.toArray)
      }
      incexc.exclude.toList match {
        case Nil =>
        case exclude :: Nil => builder.field("exclude", exclude)
        case more => builder.array("exclude", more.toArray)
      }
    }
    agg.includePartition.foreach { incpart =>
      val includeBuilder = builder.startObject("include")
      includeBuilder.field("partition", incpart.partition)
      includeBuilder.field("num_partitions", incpart.numPartitions)
      includeBuilder.endObject()
    }
    agg.heuristic.foreach(builder.field("", _))
    agg.backgroundFilter.map(QueryBuilderFn.apply).foreach{ x =>
      builder.rawField("background_filter", x)
    }
    agg.size.foreach(builder.field("size", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}
