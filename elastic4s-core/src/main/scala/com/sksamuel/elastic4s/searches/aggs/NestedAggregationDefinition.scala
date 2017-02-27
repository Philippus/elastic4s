package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition

case class NestedAggregationDefinition(name: String,
                                       path: String,
                                       pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                       subaggs: Seq[AggregationDefinition] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = NestedAggregationDefinition

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}

