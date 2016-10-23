package com.sksamuel.elastic4s.aggregations

import com.sksamuel.elastic4s.{FieldsMapper, ScriptDefinition}
import org.elasticsearch.search.aggregations.AggregationBuilders

case class ScriptedMetricAggregationDefinition(name: String) extends AbstractAggregationDefinition {

  import scala.collection.JavaConverters._

  val builder = AggregationBuilders.scriptedMetric(name)

  def initScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.initScript(script.toJavaAPI)
    this
  }

  def mapScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.mapScript(script.toJavaAPI)
    this
  }

  def combineScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.combineScript(script.toJavaAPI)
    this
  }

  def reduceScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = {
    builder.reduceScript(script.toJavaAPI)
    this
  }

  def params(params: Map[String, Any]): ScriptedMetricAggregationDefinition = {
    val mappedParams = FieldsMapper.mapper(params).asJava
    builder.params(mappedParams)
    this
  }
}
