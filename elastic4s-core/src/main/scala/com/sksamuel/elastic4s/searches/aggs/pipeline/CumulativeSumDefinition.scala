package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.cumulativesum.CumulativeSumPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class CumulativeSumDefinition(name: String,
                                   bucketsPath: String,
                                   format: Option[String] = None,
                                   metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = CumulativeSumPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.cumulativeSum(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    builder
  }

  def format(format: String): CumulativeSumDefinition = copy(format = Some(format))
  def metadata(metadata: Map[String, AnyRef]): CumulativeSumDefinition = copy(metadata = metadata)
}
