package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition

trait AggregationDefinition {

  type T <: AggregationDefinition

  def pipeline(pipeline: PipelineAggregationDefinition): T = pipelines(pipeline)
  def pipelines(first: PipelineAggregationDefinition, rest: PipelineAggregationDefinition*): T = pipelines(first +: rest)
  def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T

  def subAggregation(agg: AggregationDefinition): T = subAggregation(agg)
  def subAggregations(first: AggregationDefinition, rest: AggregationDefinition*): T = subAggregations(first +: rest)
  def subAggregations(aggs: Iterable[AggregationDefinition]): T

  def metadata(map: Map[String, AnyRef]): T
}
