package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder
import scala.collection.JavaConverters._

object ValueCountAggregationBuilder {
  def apply(agg: ValueCountAggregationDefinition): ValueCountAggregationBuilder = {
    val builder = AggregationBuilders.count(agg.name)
    agg.field.foreach(builder.field)
    agg.format.foreach(builder.format)
    agg.missing.foreach(builder.missing)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    agg.subaggs.map(AggregationBuilder.apply).foreach(builder.subAggregation)
    // todo avg.pipelines.map(AggregationBuilder.apply).foreach(builder.subAggregation)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
