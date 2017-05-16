package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.serialdiff.SerialDiffPipelineAggregationBuilder

object SerialDiffPipelineBuilder {

  import scala.collection.JavaConverters._

  def apply(p: DiffDefinition): SerialDiffPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.diff(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.map(EnumConversions.gapPolicy).foreach(builder.gapPolicy)
    p.lag.foreach(int => builder.lag(int))
    builder
  }
}
