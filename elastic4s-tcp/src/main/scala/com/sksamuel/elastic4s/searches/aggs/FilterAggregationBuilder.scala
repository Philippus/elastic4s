package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder

import scala.collection.JavaConverters._

object FilterAggregationBuilder {

  def apply(agg: FilterAggregationDefinition): FilterAggregationBuilder = {
    val builder = AggregationBuilders.filter(agg.name, QueryBuilderFn(agg.query))
    agg.subaggs.map(AggregationBuilder.apply).foreach(builder.subAggregation)
    agg.pipelines.map(PipelineAggregationBuilderFn.apply).foreach(builder.subAggregation)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
