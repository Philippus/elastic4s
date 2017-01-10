package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.{FieldsMapper, ScriptBuilder}
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder

import scala.collection.JavaConverters._

case class ScriptedMetricAggregationDefinition(name: String) extends AggregationDefinition {

  type B = ScriptedMetricAggregationBuilder
  val builder: B = AggregationBuilders.scriptedMetric(name)

  def initScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.initScript(ScriptBuilder(script))
    this
  }

  def mapScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.mapScript(ScriptBuilder(script))
    this
  }

  def combineScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.combineScript(ScriptBuilder(script))
    this
  }

  def reduceScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.reduceScript(ScriptBuilder(script))
    this
  }

  def params(params: Map[String, Any]): ScriptedMetricAggregationDefinition = {
    val mappedParams = FieldsMapper.mapper(params).asJava
    builder.params(mappedParams)
    this
  }
}
