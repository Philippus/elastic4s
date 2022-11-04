package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AggMetaDataFn, ScriptedMetricAggregation, SubAggsBuilderFn}

object ScriptedMetricAggregationBuilder {
  def apply(agg: ScriptedMetricAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("scripted_metric")
    builder.field("init_script", agg.initScript.map(_.script).getOrElse(""))
    builder.field("map_script", agg.mapScript.map(_.script).getOrElse(""))
    builder.field("combine_script", agg.combineScript.map(_.script).getOrElse(""))
    builder.field("reduce_script", agg.reduceScript.map(_.script).getOrElse(""))

    if (agg.params.nonEmpty)
      builder.autofield("params", agg.params)

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)

    builder
  }

}
