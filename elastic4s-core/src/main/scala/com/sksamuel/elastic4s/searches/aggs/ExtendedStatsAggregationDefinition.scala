package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._

case class ExtendedStatsAggregationDefinition(name: String,
                                              field: Option[String] = None,
                                              script: Option[ScriptDefinition] = None,
                                              missing: Option[String] = None,
                                              pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                              subaggs: Seq[AggregationDefinition] = Nil,
                                              metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ExtendedStatsAggregationDefinition

  def field(field: String): ExtendedStatsAggregationDefinition = copy(field = field.some)
  def script(script: ScriptDefinition): ExtendedStatsAggregationDefinition = copy(script = script.some)
  def missing(missing: String): ExtendedStatsAggregationDefinition = copy(missing = missing.some)

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
