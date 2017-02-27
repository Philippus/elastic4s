package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition

case class ChildrenAggregationDefinition(name: String,
                                         childType: String,
                                         pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                         subaggs: Seq[AggregationDefinition] = Nil,
                                         metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ChildrenAggregationDefinition

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): ChildrenAggregationDefinition = copy(metadata = metadata)
}
