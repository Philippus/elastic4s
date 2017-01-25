package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.serialdiff.SerialDiffPipelineAggregationBuilder

import scala.collection.JavaConverters._
import com.sksamuel.exts.OptionImplicits._

case class DiffDefinition(name: String,
                          bucketsPath: String,
                          format: Option[String] = None,
                          gapPolicy: Option[GapPolicy] = None,
                          lag: Option[Int] = None,
                          metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = SerialDiffPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.diff(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    lag.foreach(int => builder.lag(int))
    builder
  }

  def format(format: String): DiffDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DiffDefinition = copy(gapPolicy = Some(gapPolicy))
  def lag(lag: Int): DiffDefinition = copy(lag = lag.some)
  def metadata(metadata: Map[String, AnyRef]): DiffDefinition = copy(metadata = metadata)
}
