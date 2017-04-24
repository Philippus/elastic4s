package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class DiffDefinition(name: String,
                          bucketsPath: String,
                          format: Option[String] = None,
                          gapPolicy: Option[GapPolicy] = None,
                          lag: Option[Int] = None,
                          metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = DiffDefinition

  def format(format: String): DiffDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DiffDefinition = copy(gapPolicy = Some(gapPolicy))
  def lag(lag: Int): DiffDefinition = copy(lag = lag.some)
  def metadata(metadata: Map[String, AnyRef]): DiffDefinition = copy(metadata = metadata)
}
