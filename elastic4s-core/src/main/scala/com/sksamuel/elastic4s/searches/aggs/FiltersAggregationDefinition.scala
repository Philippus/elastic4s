package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class FiltersAggregationDefinition(name: String,
                                        filters: Iterable[QueryDefinition],
                                        pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                        subaggs: Seq[AbstractAggregation] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = FiltersAggregationDefinition

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}


