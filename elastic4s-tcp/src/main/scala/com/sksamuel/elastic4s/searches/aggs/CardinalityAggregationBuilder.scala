package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationBuilderFn
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder

import scala.collection.JavaConverters._

object CardinalityAggregationBuilder {
  def apply(agg: CardinalityAggregationDefinition): CardinalityAggregationBuilder = {
    val builder = AggregationBuilders.cardinality(agg.name)
    agg.field.foreach(builder.field)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.precisionThreshold.foreach(builder.precisionThreshold)
    agg.subaggs.map(AggregationBuilder.apply).foreach(builder.subAggregation)
    agg.pipelines.map(PipelineAggregationBuilderFn.apply).foreach(builder.subAggregation)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
