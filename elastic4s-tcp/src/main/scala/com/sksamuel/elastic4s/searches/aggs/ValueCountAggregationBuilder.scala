package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder

import scala.collection.JavaConverters._

object ValueCountAggregationBuilder {
  def apply(agg: ValueCountAggregationDefinition): ValueCountAggregationBuilder = {
    val builder = AggregationBuilders.count(agg.name)
    agg.field.foreach(builder.field)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
