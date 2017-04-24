package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class IpRangeAggregationDefinition(name: String,
                                        field: Option[String] = None,
                                        format: Option[String] = None,
                                        missing: Option[AnyRef] = None,
                                        script: Option[ScriptDefinition] = None,
                                        ranges: Seq[(Option[String], String, String)] = Nil,
                                        maskRanges: Seq[(Option[String], String)] = Nil,
                                        unboundedFrom: Option[(Option[String], String)] = None,
                                        unboundedTo: Option[(Option[String], String)] = None,
                                        subaggs: Seq[AbstractAggregation] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty) extends AggregationDefinition {

  type T = IpRangeAggregationDefinition

  def maskRange(key: String, mask: String): T = copy(maskRanges = maskRanges :+ (Some(key), mask))
  def maskRange(mask: String): T = copy(maskRanges = maskRanges :+ (None, mask))

  def range(from: String, to: String): T = copy(ranges = ranges :+ (None, from, to))
  def range(key: String, from: String, to: String): T = copy(ranges = ranges :+ (Some(key), from, to))

  def unboundedFrom(from: String): T = copy(unboundedFrom = (None, from).some)
  def unboundedFrom(key: String, from: String): T = copy(unboundedFrom = (Some(key), from).some)

  def unboundedTo(from: String): T = copy(unboundedTo = (None, from).some)
  def unboundedTo(key: String, to: String): T = copy(unboundedTo = (Some(key), to).some)

  def field(field: String): T = copy(field = field.some)
  def format(format: String): T = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: ScriptDefinition): T = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
