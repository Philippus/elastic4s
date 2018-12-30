package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.ElasticDate
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._
import org.joda.time.DateTimeZone

case class DateRangeAggregation(name: String,
                                field: Option[String] = None,
                                script: Option[Script] = None,
                                missing: Option[AnyRef] = None,
                                format: Option[String] = None,
                                timeZone: Option[DateTimeZone] = None,
                                keyed: Option[Boolean] = None,
                                ranges: Seq[(Option[String], ElasticDate, ElasticDate)] = Nil,
                                unboundedFrom: List[(Option[String], ElasticDate)] = Nil,
                                unboundedTo: List[(Option[String], ElasticDate)] = Nil,
                                subaggs: Seq[AbstractAggregation] = Nil,
                                metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = DateRangeAggregation

  def timeZone(timeZone: DateTimeZone): DateRangeAggregation = copy(timeZone = timeZone.some)
  def keyed(keyed: Boolean): DateRangeAggregation            = copy(keyed = keyed.some)
  def field(field: String): DateRangeAggregation             = copy(field = field.some)
  def script(script: Script): DateRangeAggregation           = copy(script = script.some)
  def missing(missing: AnyRef): DateRangeAggregation         = copy(missing = missing.some)

  def range(from: ElasticDate, to: ElasticDate): DateRangeAggregation = copy(ranges = ranges :+ (None, from, to))
  def range(key: String, from: ElasticDate, to: ElasticDate): DateRangeAggregation =
    copy(ranges = ranges :+ (key.some, from, to))

  def unboundedFrom(from: ElasticDate): DateRangeAggregation              = copy(unboundedFrom = unboundedFrom :+ (None, from))
  def unboundedFrom(key: String, from: ElasticDate): DateRangeAggregation = copy(unboundedFrom = unboundedFrom :+(key.some, from))

  def unboundedTo(from: ElasticDate): DateRangeAggregation              = copy(unboundedTo = unboundedTo :+ (None, from))
  def unboundedTo(key: String, from: ElasticDate): DateRangeAggregation = copy(unboundedTo = unboundedTo :+ (key.some, from))

  def format(fmt: String): DateRangeAggregation = copy(format = fmt.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
