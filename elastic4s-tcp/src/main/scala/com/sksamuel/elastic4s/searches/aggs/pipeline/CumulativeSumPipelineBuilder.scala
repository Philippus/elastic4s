package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.cumulativesum.CumulativeSumPipelineAggregationBuilder

object CumulativeSumPipelineBuilder {

  import scala.collection.JavaConverters._

  def apply(p: CumulativeSumDefinition): CumulativeSumPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.cumulativeSum(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    builder
  }
}
