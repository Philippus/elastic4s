package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder

import scala.collection.JavaConverters._

object StatsAggregationBuilder {

  def apply(agg: StatsAggregationDefinition): StatsAggregationBuilder = {
    val builder = AggregationBuilders.stats(agg.name)
    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
