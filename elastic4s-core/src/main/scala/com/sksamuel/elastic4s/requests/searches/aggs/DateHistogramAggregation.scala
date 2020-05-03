package com.sksamuel.elastic4s.requests.searches.aggs

import java.util.TimeZone

import com.sksamuel.elastic4s.ElasticDate
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.pipeline.PipelineAgg
import com.sksamuel.exts.OptionImplicits._

import scala.concurrent.duration.{FiniteDuration, _}

case class HistogramOrder(name: String, asc: Boolean)
object HistogramOrder {
  val KEY_ASC: HistogramOrder = HistogramOrder("_key", asc = true)
  val KEY_DESC: HistogramOrder = HistogramOrder("_key", asc = false)
  val COUNT_ASC: HistogramOrder = HistogramOrder("_count", asc = true)
  val COUNT_DESC: HistogramOrder = HistogramOrder("_count", asc = false)
}

case class DateHistogramAggregation(name: String,
                                    calendarInterval: Option[DateHistogramInterval] = None,
                                    fixedInterval: Option[DateHistogramInterval] = None,
                                    interval: Option[DateHistogramInterval] = None,
                                    minDocCount: Option[Long] = None,
                                    timeZone: Option[TimeZone] = None,
                                    order: Option[HistogramOrder] = None,
                                    keyed: Option[Boolean] = None,
                                    offset: Option[String] = None,
                                    format: Option[String] = None,
                                    field: Option[String] = None,
                                    script: Option[Script] = None,
                                    missing: Option[Any] = None,
                                    extendedBounds: Option[ExtendedBounds] = None,
                                    pipelines: Seq[PipelineAgg] = Nil,
                                    subaggs: Seq[AbstractAggregation] = Nil,
                                    metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = DateHistogramAggregation

  def extendedBounds(bounds: ExtendedBounds): DateHistogramAggregation = copy(extendedBounds = bounds.some)
  def extendedBounds(min: ElasticDate, max: ElasticDate): DateHistogramAggregation =
    copy(extendedBounds = ExtendedBounds(min, max).some)

  def calendarInterval(calendarInterval: DateHistogramInterval): DateHistogramAggregation = copy(calendarInterval = calendarInterval.some)

  def fixedInterval(seconds: Long): DateHistogramAggregation                   = fixedInterval(seconds.seconds)
  def fixedInterval(dur: FiniteDuration): DateHistogramAggregation             = fixedInterval(DateHistogramInterval.seconds(dur.toSeconds))
  def fixedInterval(fixedInterval: DateHistogramInterval): DateHistogramAggregation = copy(fixedInterval = fixedInterval.some)

  def minDocCount(min: Long): DateHistogramAggregation = copy(minDocCount = min.some)

  def timeZone(timeZone: TimeZone): DateHistogramAggregation = copy(timeZone = timeZone.some)
  def offset(offset: String): DateHistogramAggregation           = copy(offset = offset.some)
  def keyed(keyed: Boolean): T                                   = copy(keyed = keyed.some)

  def order(order: HistogramOrder): DateHistogramAggregation = copy(order = order.some)

  def format(format: String): DateHistogramAggregation = copy(format = format.some)
  def field(field: String): DateHistogramAggregation   = copy(field = field.some)
  def script(script: Script): DateHistogramAggregation = copy(script = script.some)
  def missing(missing: Any): DateHistogramAggregation  = copy(missing = missing.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
