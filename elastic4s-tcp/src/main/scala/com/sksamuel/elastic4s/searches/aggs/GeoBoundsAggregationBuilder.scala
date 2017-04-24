package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBoundsAggregationBuilder

import scala.collection.JavaConverters._

object GeoBoundsAggregationBuilder {

  def apply(agg: GeoBoundsAggregationDefinition): GeoBoundsAggregationBuilder = {

    val builder = AggregationBuilders.geoBounds(agg.name)

    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.wrapLongitude.foreach(builder.wrapLongitude)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
