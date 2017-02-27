package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._

case class SumAggregationDefinition(name: String,
                                    field: Option[String] = None,
                                    missing: Option[String] = None,
                                    script: Option[ScriptDefinition] = None,
                                    pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                    subaggs: Seq[AggregationDefinition] = Nil,
                                    metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = SumAggregationDefinition

  def field(field: String): SumAggregationDefinition = copy(field = field.some)
  def missing(missing: String): SumAggregationDefinition = copy(missing = missing.some)
  def script(script: ScriptDefinition): SumAggregationDefinition = copy(script = script.some)

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
