package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class RangeAggregationDefinition(name: String,
                                      field: Option[String] = None,
                                      format: Option[String] = None,
                                      missing: Option[AnyRef] = None,
                                      keyed: Option[Boolean] = None,
                                      script: Option[ScriptDefinition] = None,
                                      ranges: Seq[(Option[String], Double, Double)] = Nil,
                                      unboundedFrom: Option[(Option[String], Double)] = None,
                                      unboundedTo: Option[(Option[String], Double)] = None,
                                      subaggs: Seq[AbstractAggregation] = Nil,
                                      metadata: Map[String, AnyRef] = Map.empty) extends AggregationDefinition {

  type T = RangeAggregationDefinition

  def ranges(ranges: (Double, Double)*): T = copy(ranges = ranges.map { case (from, to) => (None, from, to) })

  def range(from: Double, to: Double): T = copy(ranges = ranges :+ (None, from, to))
  def range(key: String, from: Double, to: Double): T = copy(ranges = ranges :+ (Some(key), from, to))

  def unboundedFrom(from: Double): T = copy(unboundedFrom = (None, from).some)
  def unboundedFrom(key: String, from: Double): T = copy(unboundedFrom = (Some(key), from).some)

  def unboundedTo(from: Double): T = copy(unboundedTo = (None, from).some)
  def unboundedTo(key: String, to: Double): T = copy(unboundedTo = (Some(key), to).some)

  def field(field: String): T = copy(field = field.some)
  def format(format: String): T = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: ScriptDefinition): T = copy(script = script.some)
  def keyed(keyed: Boolean): T = copy(keyed = keyed.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
