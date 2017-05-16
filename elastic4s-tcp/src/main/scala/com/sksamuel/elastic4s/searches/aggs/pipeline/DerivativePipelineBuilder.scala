package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.derivative.DerivativePipelineAggregationBuilder

object DerivativePipelineBuilder {

  import scala.collection.JavaConverters._

  def apply(pipeline: DerivativeDefinition): DerivativePipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.derivative(pipeline.name, pipeline.bucketsPath)
    if (pipeline.metadata.nonEmpty) builder.setMetaData(pipeline.metadata.asJava)
    pipeline.format.foreach(builder.format)
    pipeline.gapPolicy.map(EnumConversions.gapPolicy).foreach(builder.gapPolicy)
    pipeline.unit.map(_.toSeconds.toInt).map(DateHistogramInterval.seconds).foreach(builder.unit)
    pipeline.unitString.foreach(builder.unit)
    builder
  }
}
