package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.ScriptedMetricAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object ScriptedMetricAggregationBuilder {
  def apply(agg: ScriptedMetricAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("scripted_metric")
    if (agg.params.nonEmpty) {
      builder.startObject("params")
      builder.startObject("_agg").endObject()
      agg.params.foreach(param => builder.field(param._1, param._2))
      builder.endObject()
    }
    agg.initScript.foreach(s => builder.rawField("init_script", ScriptBuilderFn(s).bytes(), XContentType.JSON))
    agg.mapScript.foreach(s => builder.rawField("map_script", ScriptBuilderFn(s).bytes(), XContentType.JSON))
    agg.combineScript.foreach(s => builder.rawField("combine_script", ScriptBuilderFn(s).bytes(), XContentType.JSON))
    agg.reduceScript.foreach(s => builder.rawField("reduce_script", ScriptBuilderFn(s).bytes, XContentType.JSON))
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
