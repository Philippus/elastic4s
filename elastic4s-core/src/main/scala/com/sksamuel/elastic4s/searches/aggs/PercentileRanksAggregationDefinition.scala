package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesMethod

case class PercentileRanksAggregationDefinition(name: String,
                                                field: Option[String] = None,
                                                format: Option[String] = None,
                                                missing: Option[AnyRef] = None,
                                                values: Seq[Double] = Nil,
                                                method: Option[PercentilesMethod] = None,
                                                keyed: Option[Boolean] = None,
                                                numberOfSignificantValueDigits: Option[Int] = None,
                                                compression: Option[Double] = None,
                                                script: Option[ScriptDefinition] = None,
                                                subaggs: Seq[AbstractAggregation] = Nil,
                                                metadata: Map[String, AnyRef] = Map.empty) extends AggregationDefinition {

  type T = PercentileRanksAggregationDefinition

  def field(field: String): T = copy(field = field.some)
  def format(format: String): T = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: ScriptDefinition): T = copy(script = script.some)
  def keyed(keyed: Boolean): T = copy(keyed = keyed.some)
  def numberOfSignificantValueDigits(digits: Int): T = copy(numberOfSignificantValueDigits = digits.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)

  @deprecated("use values", "5.0.0")
  def percents(first: Double, rest: Double*): PercentileRanksAggregationDefinition = values(first +: rest)
  def values(first: Double, rest: Double*): PercentileRanksAggregationDefinition = values(first +: rest)

  @deprecated("use values", "5.0.0")
  def percents(values: Iterable[Double]): PercentileRanksAggregationDefinition = copy(values = values.toSeq)
  def values(values: Iterable[Double]): PercentileRanksAggregationDefinition = copy(values = values.toSeq)

  def method(method: PercentilesMethod): PercentileRanksAggregationDefinition = copy(method = method.some)
  def compression(compression: Double): PercentileRanksAggregationDefinition = copy(compression = compression.some)
}
