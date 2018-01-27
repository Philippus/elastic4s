package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude

import scala.collection.JavaConverters._

object SigTermsAggregationBuilder {
  def apply(agg: SigTermsAggregationDefinition): SignificantTermsAggregationBuilder = {
    val builder = AggregationBuilders.significantTerms(agg.name)
    agg.minDocCount.foreach(builder.minDocCount)
    agg.executionHint.foreach(builder.executionHint)
    agg.size.foreach(builder.size)
    agg.includeExclude.foreach { it =>
      builder.includeExclude(new IncludeExclude(it.include.toArray, it.exclude.toArray))
    }
    agg.includePartition.foreach { it =>
      builder.includeExclude(new IncludeExclude(it.partition, it.numPartitions))
    }
    agg.field.foreach(builder.field)
    agg.shardMinDocCount.foreach(builder.shardMinDocCount)
    agg.shardSize.foreach(builder.shardSize)
    agg.backgroundFilter.map(QueryBuilderFn.apply).foreach(builder.backgroundFilter)

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    // agg.heuristic.foreach(builder.significanceHeuristic)
    builder
  }
}
