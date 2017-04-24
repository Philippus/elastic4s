package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroidAggregationBuilder

import scala.collection.JavaConverters._

object GeoCentroidAggregationBuilder {

  def apply(agg: GeoCentroidAggregationDefinition): GeoCentroidAggregationBuilder = {
    val builder = AggregationBuilders.geoCentroid(agg.name)
    agg.field.foreach(builder.field)
    agg.missing.foreach(builder.missing)
    agg.format.foreach(builder.format)
    agg.valueType.foreach(builder.valueType)
    agg.script.map(ScriptBuilder.apply).foreach(builder.script)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
