package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.FieldsMapper
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder

import scala.collection.JavaConverters._

case class ScriptedMetricAggregationDefinition(name: String) extends AggregationDefinition {

  type B = ScriptedMetricAggregationBuilder
  val builder: B = AggregationBuilders.scriptedMetric(name)

  def initScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.initScript(script.build)
    this
  }

  def mapScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.mapScript(script.build)
    this
  }

  def combineScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.combineScript(script.build)
    this
  }

  def reduceScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.reduceScript(script.build)
    this
  }

  def params(params: Map[String, Any]): ScriptedMetricAggregationDefinition = {
    val mappedParams = FieldsMapper.mapper(params).asJava
    builder.params(mappedParams)
    this
  }
}
