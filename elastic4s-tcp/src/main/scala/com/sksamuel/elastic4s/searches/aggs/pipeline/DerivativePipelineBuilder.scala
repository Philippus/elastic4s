package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.derivative.DerivativePipelineAggregationBuilder

object DerivativePipelineBuilder {

  import scala.collection.JavaConverters._

  def apply(pipeline: DerivativeDefinition): DerivativePipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.derivative(pipeline.name, pipeline.bucketsPath)
    if (pipeline.metadata.nonEmpty) builder.setMetaData(pipeline.metadata.asJava)
    pipeline.format.foreach(builder.format)
    pipeline.gapPolicy.foreach(builder.gapPolicy)
    pipeline.unit.foreach(builder.unit)
    pipeline.unitString.foreach(builder.unit)
    builder
  }
}
