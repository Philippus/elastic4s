package com.sksamuel.elastic4s.requests.searches.aggs

import java.util.TimeZone

import com.sksamuel.elastic4s.requests.searches.aggs.pipeline.PipelineAgg
import com.sksamuel.exts.OptionImplicits._

case class AutoDateHistogramAggregation(name: String,
                                        field: String,
                                        buckets: Option[Int] = None,
                                        format: Option[String] = None,
                                        minimumInterval: Option[String] = None,
                                        timeZone: Option[TimeZone] = None,
                                        missing: Option[Any] = None,
                                        pipelines: Seq[PipelineAgg] = Nil,
                                        subaggs: Seq[AbstractAggregation] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty)
  extends Aggregation {

  type T = AutoDateHistogramAggregation

  def timeZone(timeZone: TimeZone): T = copy(timeZone = timeZone.some)
  def buckets(buckets: Int): T = copy(buckets = buckets.some)
  def minimumInterval(minimumInterval: String): T = copy(minimumInterval = minimumInterval.some)

  def format(format: String): T = copy(format = format.some)
  def missing(missing: Any): T = copy(missing = missing.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
