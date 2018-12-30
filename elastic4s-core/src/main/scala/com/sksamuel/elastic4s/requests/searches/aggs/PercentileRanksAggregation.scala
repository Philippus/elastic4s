package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

sealed trait PercentilesMethod
object PercentilesMethod {
  case object TDigest extends PercentilesMethod
  case object HDR     extends PercentilesMethod
}

case class PercentileRanksAggregation(name: String,
                                      field: Option[String] = None,
                                      format: Option[String] = None,
                                      missing: Option[AnyRef] = None,
                                      values: Seq[Double] = Nil,
                                      method: Option[PercentilesMethod] = None,
                                      keyed: Option[Boolean] = None,
                                      numberOfSignificantValueDigits: Option[Int] = None,
                                      compression: Option[Double] = None,
                                      script: Option[Script] = None,
                                      subaggs: Seq[AbstractAggregation] = Nil,
                                      metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = PercentileRanksAggregation

  def field(field: String): T                        = copy(field = field.some)
  def format(format: String): T                      = copy(format = format.some)
  def missing(missing: AnyRef): T                    = copy(missing = missing.some)
  def script(script: Script): T                      = copy(script = script.some)
  def keyed(keyed: Boolean): T                       = copy(keyed = keyed.some)
  def numberOfSignificantValueDigits(digits: Int): T = copy(numberOfSignificantValueDigits = digits.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)

  def values(first: Double, rest: Double*): PercentileRanksAggregation = values(first +: rest)
  def values(values: Iterable[Double]): PercentileRanksAggregation     = copy(values = values.toSeq)

  def method(method: PercentilesMethod): PercentileRanksAggregation = copy(method = method.some)
  def compression(compression: Double): PercentileRanksAggregation  = copy(compression = compression.some)
}
