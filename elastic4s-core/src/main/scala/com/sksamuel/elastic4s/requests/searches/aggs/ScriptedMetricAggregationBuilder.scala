package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ScriptedMetricAggregationBuilder {
  def apply(agg: ScriptedMetricAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("scripted_metric")
    builder.field("init_script", agg.initScript.map(_.script).getOrElse(""))
    builder.field("map_script", agg.mapScript.map(_.script).getOrElse(""))
    builder.field("combine_script", agg.combineScript.map(_.script).getOrElse(""))
    builder.field("reduce_script", agg.reduceScript.map(_.script).getOrElse(""))

    if (agg.params.nonEmpty)
      builder.autofield("params", agg.params)

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }

}
