package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.{EnumConversions, ScriptBuilder}
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanksAggregationBuilder

import scala.collection.JavaConverters._

object PercentileRanksAggregationBuilder {

  def apply(agg: PercentileRanksAggregationDefinition): PercentileRanksAggregationBuilder = {

    val builder = AggregationBuilders.percentileRanks(agg.name, agg.values.toArray)

    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.keyed.foreach(builder.keyed)
    agg.compression.foreach(builder.compression)
    agg.method.map(EnumConversions.percentilesMethod).foreach(builder.method)
    agg.numberOfSignificantValueDigits.foreach(builder.numberOfSignificantValueDigits)

    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
