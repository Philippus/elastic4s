package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanksAggregationBuilder

import scala.collection.JavaConverters._

object PercentileRanksAggregationBuilder {

  def apply(agg: PercentileRanksAggregationDefinition): PercentileRanksAggregationBuilder = {

    val builder = AggregationBuilders.percentileRanks(agg.name)

    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.keyed.foreach(builder.keyed)
    agg.compression.foreach(builder.compression)
    agg.method.foreach(builder.method)
    agg.numberOfSignificantValueDigits.foreach(builder.numberOfSignificantValueDigits)

    if (agg.values.nonEmpty) builder.values(agg.values: _*)

    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
