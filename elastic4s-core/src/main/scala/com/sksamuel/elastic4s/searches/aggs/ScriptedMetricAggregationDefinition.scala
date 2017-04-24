package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class ScriptedMetricAggregationDefinition(name: String,
                                               initScript: Option[ScriptDefinition] = None,
                                               mapScript: Option[ScriptDefinition] = None,
                                               combineScript: Option[ScriptDefinition] = None,
                                               reduceScript: Option[ScriptDefinition] = None,
                                               params: Map[String, AnyRef] = Map.empty,
                                               subaggs: Seq[AbstractAggregation] = Nil,
                                               metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ScriptedMetricAggregationDefinition

  def initScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = copy(initScript = script.some)
  def mapScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = copy(mapScript = script.some)
  def combineScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = copy(combineScript = script.some)
  def reduceScript(script: ScriptDefinition): ScriptedMetricAggregationDefinition = copy(reduceScript = script.some)

  def params(params: Map[String, AnyRef]): ScriptedMetricAggregationDefinition = copy(params = params)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
