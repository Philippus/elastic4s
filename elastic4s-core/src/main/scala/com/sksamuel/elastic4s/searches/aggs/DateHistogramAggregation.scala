package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ElasticDate
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.DateHistogramInterval
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._
import org.joda.time.DateTimeZone

import scala.concurrent.duration.{FiniteDuration, _}

case class HistogramOrder(name: String, asc: Boolean)
object HistogramOrder {
  val KEY_ASC = HistogramOrder("_key", true)
  val KEY_DESC = HistogramOrder("_key", false)
  val COUNT_ASC = HistogramOrder("_count", true)
  val COUNT_DESC = HistogramOrder("_count", false)
}


case class DateHistogramAggregation(name: String,
                                    interval: Option[DateHistogramInterval] = None,
                                    minDocCount: Option[Long] = None,
                                    timeZone: Option[DateTimeZone] = None,
                                    order: Option[HistogramOrder] = None,
                                    offset: Option[String] = None,
                                    format: Option[String] = None,
                                    field: Option[String] = None,
                                    script: Option[ScriptDefinition] = None,
                                    missing: Option[Any] = None,
                                    extendedBounds: Option[ExtendedBounds] = None,
                                    pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                    subaggs: Seq[AbstractAggregation] = Nil,
                                    metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = DateHistogramAggregation

  def extendedBounds(bounds: ExtendedBounds): DateHistogramAggregation = copy(extendedBounds = bounds.some)
  def extendedBounds(min: ElasticDate, max: ElasticDate): DateHistogramAggregation = copy(extendedBounds = ExtendedBounds(min, max).some)

  def interval(seconds: Long): DateHistogramAggregation = interval(seconds.seconds)
  def interval(dur: FiniteDuration): DateHistogramAggregation = interval(DateHistogramInterval.seconds(dur.toSeconds))
  def interval(interval: DateHistogramInterval): DateHistogramAggregation = copy(interval = interval.some)

  def minDocCount(min: Long): DateHistogramAggregation = copy(minDocCount = min.some)

  def timeZone(timeZone: DateTimeZone): DateHistogramAggregation = copy(timeZone = timeZone.some)
  def offset(offset: String): DateHistogramAggregation = copy(offset = offset.some)

  def order(order: HistogramOrder): DateHistogramAggregation = copy(order = order.some)

  def format(format: String): DateHistogramAggregation = copy(format = format.some)
  def field(field: String): DateHistogramAggregation = copy(field = field.some)
  def script(script: ScriptDefinition): DateHistogramAggregation = copy(script = script.some)
  def missing(missing: Any): DateHistogramAggregation = copy(missing = missing.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)
}
