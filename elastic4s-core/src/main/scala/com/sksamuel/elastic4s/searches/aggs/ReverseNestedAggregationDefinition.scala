package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._

case class ReverseNestedAggregationDefinition(name: String,
                                              path: Option[String] = None,
                                              pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                              subaggs: Seq[AggregationDefinition] = Nil,
                                              metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ReverseNestedAggregationDefinition

  def path(path: String): ReverseNestedAggregationDefinition = copy(path = path.some)

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T =    copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T =    copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
