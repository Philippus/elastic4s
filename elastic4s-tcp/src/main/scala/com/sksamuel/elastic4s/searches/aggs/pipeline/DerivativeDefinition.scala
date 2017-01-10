package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.derivative.DerivativePipelineAggregationBuilder
import scala.collection.JavaConverters._

case class DerivativeDefinition(name: String,
                                bucketsPath: String,
                                format: Option[String] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                unit: Option[DateHistogramInterval] = None,
                                unitString: Option[String] = None,
                                metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = DerivativePipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.derivative(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    unit.foreach(builder.unit)
    unitString.foreach(builder.unit)
    builder
  }

  def unit(unit: DateHistogramInterval): DerivativeDefinition = copy(unit = Some(unit))
  def unit(unit: String): DerivativeDefinition = copy(unitString = Some(unit))

  def format(format: String): DerivativeDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DerivativeDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): DerivativeDefinition = copy(metadata = metadata)
}
