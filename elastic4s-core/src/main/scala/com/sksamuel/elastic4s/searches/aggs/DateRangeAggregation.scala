package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._
import org.joda.time.DateTimeZone

case class DateRangeAggregation(name: String,
                                field: Option[String] = None,
                                script: Option[ScriptDefinition] = None,
                                missing: Option[AnyRef] = None,
                                format: Option[String] = None,
                                timeZone: Option[DateTimeZone] = None,
                                keyed: Option[Boolean] = None,
                                ranges: Seq[(Option[String], Any, Any)] = Nil,
                                unboundedFrom: Option[(Option[String], Any)] = None,
                                unboundedTo: Option[(Option[String], Any)] = None,
                                pipelines: Seq[PipelineAggregationDefinition] = Nil,
                                subaggs: Seq[AggregationDefinition] = Nil,
                                metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = DateRangeAggregation

  def timeZone(timeZone: DateTimeZone): DateRangeAggregation = copy(timeZone = timeZone.some)
  def keyed(keyed: Boolean): DateRangeAggregation = copy(keyed = keyed.some)
  def field(field: String): DateRangeAggregation = copy(field = field.some)
  def script(script: ScriptDefinition): DateRangeAggregation = copy(script = script.some)
  def missing(missing: AnyRef): DateRangeAggregation = copy(missing = missing.some)

  def range(from: String, to: String): DateRangeAggregation = copy(ranges = ranges :+ (None, from, to))
  def range(key: String, from: String, to: String): DateRangeAggregation = copy(ranges = ranges :+ (key.some, from, to))
  def range(from: Long, to: Long): DateRangeAggregation = copy(ranges = ranges :+ (None, from, to))
  def range(key: String, from: Long, to: Long): DateRangeAggregation = copy(ranges = ranges :+ (key.some, from, to))

  def unboundedFrom(from: String): DateRangeAggregation = copy(unboundedFrom = (None, from).some)
  def unboundedFrom(key: String, from: String): DateRangeAggregation = copy(unboundedFrom = (key.some, from).some)
  def unboundedFrom(from: Long): DateRangeAggregation = copy(unboundedFrom = (None, from).some)
  def unboundedFrom(key: String, from: Long): DateRangeAggregation = copy(unboundedFrom = (key.some, from).some)

  def unboundedTo(from: String): DateRangeAggregation = copy(unboundedTo = (None, from).some)
  def unboundedTo(key: String, from: String): DateRangeAggregation = copy(unboundedTo = (key.some, from).some)
  def unboundedTo(from: Long): DateRangeAggregation = copy(unboundedTo = (None, from).some)
  def unboundedTo(key: String, from: Long): DateRangeAggregation = copy(unboundedTo = (key.some, from).some)

  def format(fmt: String): DateRangeAggregation = copy(format = fmt.some)

  override def pipelines(pipelines: Iterable[PipelineAggregationDefinition]): T = copy(pipelines = pipelines.toSeq)
  override def subAggregations(aggs: Iterable[AggregationDefinition]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
