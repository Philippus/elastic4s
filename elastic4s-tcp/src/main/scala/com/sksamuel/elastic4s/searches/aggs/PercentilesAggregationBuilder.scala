package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder

import scala.collection.JavaConverters._

object PercentilesAggregationBuilder {

  def apply(agg: PercentilesAggregationDefinition): PercentilesAggregationBuilder = {
    val builder = AggregationBuilders.percentiles(agg.name)
    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.numberOfSignificantValueDigits.foreach(builder.numberOfSignificantValueDigits)
    agg.format.foreach(builder.format)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
