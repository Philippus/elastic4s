package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.children.ChildrenAggregationBuilder

import scala.collection.JavaConverters._

object ChildrenAggregationBuilder {
  def apply(agg: ChildrenAggregationDefinition): ChildrenAggregationBuilder = {
    val builder = AggregationBuilders.children(agg.name, agg.childType)
    agg.subaggs.map(AggregationBuilder.apply).foreach(builder.subAggregation)
    agg.pipelines.map(PipelineAggregationBuilderFn.apply).foreach(builder.subAggregation)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
