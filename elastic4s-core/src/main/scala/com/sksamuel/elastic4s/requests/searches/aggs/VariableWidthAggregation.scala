package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.pipeline.PipelineAgg
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class VariableWidthAggregation(
    name: String,
    field: String,
    buckets: Option[Int] = None,
    shardSize: Option[Int] = None,
    initialBuffer: Option[Int] = None,
    missing: Option[Any] = None,
    pipelines: Seq[PipelineAgg] = Nil,
    subaggs: Seq[AbstractAggregation] = Nil,
    metadata: Map[String, AnyRef] = Map.empty
) extends Aggregation {

  type T = VariableWidthAggregation

  def buckets(buckets: Int): T             = copy(buckets = buckets.some)
  def shardSize(shardSize: Int): T         = copy(shardSize = shardSize.some)
  def initialBuffer(initialBuffer: Int): T = copy(initialBuffer = initialBuffer.some)
  def missing(missing: Any): T             = copy(missing = missing.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
