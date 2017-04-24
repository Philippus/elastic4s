package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder

import scala.collection.JavaConverters._

object ExtendedStatsAggregationBuilder {

  def apply(agg: ExtendedStatsAggregationDefinition): ExtendedStatsAggregationBuilder = {
    val builder = AggregationBuilders.extendedStats(agg.name)
    agg.field.foreach(builder.field)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
