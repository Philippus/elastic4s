package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder

import scala.collection.JavaConverters._

object ScriptedMetricAggregationBuilder {

  def apply(agg: ScriptedMetricAggregationDefinition): ScriptedMetricAggregationBuilder = {
    val builder = AggregationBuilders.scriptedMetric(agg.name)

    agg.initScript.map(ScriptBuilder.apply).foreach(builder.initScript)
    agg.combineScript.map(ScriptBuilder.apply).foreach(builder.initScript)
    agg.mapScript.map(ScriptBuilder.apply).foreach(builder.initScript)
    agg.reduceScript.map(ScriptBuilder.apply).foreach(builder.initScript)

    if (agg.params.nonEmpty) builder.params(agg.params.asJava)

    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)
    builder
  }
}
