package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.movavg.MovAvgPipelineAggregationBuilder

import scala.collection.JavaConverters._

object MovAvgPipelineBuilder {

  def apply(p: MovAvgDefinition): MovAvgPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.movingAvg(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.foreach(builder.gapPolicy)
    p.minimise.foreach(builder.minimize)
    p.modelBuilder.foreach(builder.modelBuilder)
    p.numPredictions.foreach(num => builder.predict(num))
    p.window.foreach(win => builder.window(win))
    builder
  }
}
