package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition

trait AggregationDefinition {

  type T <: AggregationDefinition

  def name: String
  def subaggs: Seq[AggregationDefinition]
  def metadata: Map[String, AnyRef]

  def pipeline(pipeline: PipelineAggregationDefinition): T = pipelines(pipeline)
  def pipelines(first: PipelineAggregationDefinition, rest: PipelineAggregationDefinition*): T = pipelines(first +: rest)
  def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T

  def subagg(agg: AggregationDefinition): T = subaggs(agg)
  def subaggs(first: AggregationDefinition, rest: AggregationDefinition*): T = subaggs(first +: rest)
  def subaggs(aggs: Iterable[AggregationDefinition]): T = subAggregations(aggs)

  def subAggregation(agg: AggregationDefinition): T = subAggregations(agg)
  def subAggregations(first: AggregationDefinition, rest: AggregationDefinition*): T = subAggregations(first +: rest)
  def subAggregations(aggs: Iterable[AggregationDefinition]): T

  def metadata(map: Map[String, AnyRef]): T
}
