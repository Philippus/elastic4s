package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder

object NestedAggregationBuilder {

  import scala.collection.JavaConverters._

  def apply(agg: NestedAggregationDefinition): NestedAggregationBuilder = {

    val builder = AggregationBuilders.nested(agg.name, agg.path)
    agg.subaggs.map(AggregationBuilder.apply).foreach(builder.subAggregation)
    agg.pipelines.map(PipelineAggregationBuilderFn.apply).foreach(builder.subAggregation)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
