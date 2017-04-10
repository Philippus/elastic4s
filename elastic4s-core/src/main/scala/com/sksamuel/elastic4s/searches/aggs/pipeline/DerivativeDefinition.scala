package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class DerivativeDefinition(name: String,
                                bucketsPath: String,
                                format: Option[String] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                unit: Option[DateHistogramInterval] = None,
                                unitString: Option[String] = None,
                                metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = DerivativeDefinition

  def unit(unit: DateHistogramInterval): DerivativeDefinition = copy(unit = Some(unit))
  def unit(unit: String): DerivativeDefinition = copy(unitString = Some(unit))

  def format(format: String): DerivativeDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DerivativeDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): DerivativeDefinition = copy(metadata = metadata)
}
