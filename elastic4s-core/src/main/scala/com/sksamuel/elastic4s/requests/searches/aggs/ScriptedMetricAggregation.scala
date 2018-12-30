package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class ScriptedMetricAggregation(name: String,
                                     initScript: Option[Script] = None,
                                     mapScript: Option[Script] = None,
                                     combineScript: Option[Script] = None,
                                     reduceScript: Option[Script] = None,
                                     params: Map[String, AnyRef] = Map.empty,
                                     subaggs: Seq[AbstractAggregation] = Nil,
                                     metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = ScriptedMetricAggregation

  def initScript(script: Script): ScriptedMetricAggregation    = copy(initScript = script.some)
  def mapScript(script: Script): ScriptedMetricAggregation     = copy(mapScript = script.some)
  def combineScript(script: Script): ScriptedMetricAggregation = copy(combineScript = script.some)
  def reduceScript(script: Script): ScriptedMetricAggregation  = copy(reduceScript = script.some)

  def params(params: Map[String, AnyRef]): ScriptedMetricAggregation = copy(params = params)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
