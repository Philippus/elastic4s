package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder

import scala.collection.JavaConverters._

object CardinalityAggregationBuilder {
  def apply(agg: CardinalityAggregationDefinition): CardinalityAggregationBuilder = {
    val builder = AggregationBuilders.cardinality(agg.name)
    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.precisionThreshold.foreach(builder.precisionThreshold)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
