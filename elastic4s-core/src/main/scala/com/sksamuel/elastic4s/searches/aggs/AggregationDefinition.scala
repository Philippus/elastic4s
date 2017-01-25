package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import org.elasticsearch.search.aggregations._

trait AggregationDefinition {

  type B <: AggregationBuilder
  val builder: B

  def pipeline(pipeline: PipelineAggregationDefinition): this.type = {
    builder.subAggregation(pipeline.builder)
    this
  }

  def pipelines(first: PipelineAggregationDefinition,
                rest: PipelineAggregationDefinition*): this.type = pipelines(first +: rest)

  def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): this.type = {
    pipelines.foreach(pipeline)
    this
  }

  def subAggregation(agg: AggregationDefinition): this.type = {
    builder.subAggregation(agg.builder)
    this
  }

  def subAggregations(first: AggregationDefinition, rest: AggregationDefinition*): this.type =
    subAggregations(first +: rest)

  def subAggregations(aggs: Iterable[AggregationDefinition]): this.type = {
    aggs.foreach(subAggregation)
    this
  }

  @deprecated("use subAggregations", "5.0.0")
  def aggs(first: AggregationDefinition, rest: AggregationDefinition*): this.type =
    subAggregations(first +: rest)

  @deprecated("use subAggregations", "5.0.0")
  def aggs(aggs: Iterable[AggregationDefinition]): this.type = {
    aggs.foreach(subAggregation)
    this
  }
}
