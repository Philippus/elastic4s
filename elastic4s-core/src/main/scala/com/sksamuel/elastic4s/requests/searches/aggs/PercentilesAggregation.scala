package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class PercentilesAggregation(name: String,
                                  field: Option[String] = None,
                                  missing: Option[AnyRef] = None,
                                  format: Option[String] = None,
                                  script: Option[Script] = None,
                                  numberOfSignificantValueDigits: Option[Int] = None,
                                  percents: Seq[Double] = Nil,
                                  compression: Option[Double] = None,
                                  keyed: Option[Boolean] = None,
                                  subaggs: Seq[AbstractAggregation] = Nil,
                                  metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = PercentilesAggregation

  def percents(first: Double, rest: Double*): T = percents(first +: rest)
  def percents(percents: Iterable[Double]): T   = copy(percents = percents.toSeq)

  def compression(compression: Double): T = copy(compression = compression.some)

  def keyed(keyed: Boolean): T    = copy(keyed = keyed.some)
  def format(format: String): T   = copy(format = format.some)
  def field(field: String): T     = copy(field = field.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: Script): T   = copy(script = script.some)
  def numberOfSignificantValueDigits(numberOfSignificantValueDigits: Int): T =
    copy(numberOfSignificantValueDigits = numberOfSignificantValueDigits.some)
  def hdr(numberOfSignificantValueDigits: Int): T =
    copy(numberOfSignificantValueDigits = numberOfSignificantValueDigits.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T    = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): PercentilesAggregation = copy(metadata = metadata)

}
