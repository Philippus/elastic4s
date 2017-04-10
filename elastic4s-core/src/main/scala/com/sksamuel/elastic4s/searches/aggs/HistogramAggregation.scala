package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram

case class HistogramAggregation(name: String,
                                field: Option[String] = None,
                                format: Option[String] = None,
                                missing: Option[AnyRef] = None,
                                minDocCount: Option[Long] = None,
                                interval: Option[Double] = None,
                                keyed: Option[Boolean] = None,
                                offset: Option[Double] = None,
                                extendedBounds: Option[(Double, Double)] = None,
                                order: Option[Histogram.Order] = None,
                                script: Option[ScriptDefinition] = None,
                                subaggs: Seq[AbstractAggregation] = Nil,
                                metadata: Map[String, AnyRef] = Map.empty) extends AggregationDefinition {

  type T = HistogramAggregation

  def field(field: String): T = copy(field = field.some)
  def format(format: String): T = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: ScriptDefinition): T = copy(script = script.some)
  def keyed(keyed: Boolean): T = copy(keyed = keyed.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)

  def interval(interval: Double): HistogramAggregation = copy(interval = interval.some)
  def minDocCount(min: Long): HistogramAggregation = copy(minDocCount = min.some)

  def order(order: Histogram.Order): HistogramAggregation = copy(order = order.some)
  def offset(offset: Double): HistogramAggregation = copy(offset = offset.some)
  def extendedBounds(min: Double, max: Double): HistogramAggregation = copy(extendedBounds = (min, max).some)
}
