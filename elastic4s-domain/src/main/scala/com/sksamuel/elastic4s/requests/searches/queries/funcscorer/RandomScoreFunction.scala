package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class SeedAndField(seed: Long, fieldName: String = SeedAndField.DefaultFieldName)
object SeedAndField        {
  final val DefaultFieldName = "_seq_no"
}
case class RandomScoreFunction(
    seedAndField: Option[SeedAndField],
    weight: Option[Double],
    override val filter: Option[Query]
) extends ScoreFunction {

  def fieldName(fieldName: String): RandomScoreFunction = copy(seedAndField =
    Some(seedAndField.getOrElse(SeedAndField(seed = System.nanoTime())).copy(fieldName = fieldName))
  )
  def weight(weight: Double): RandomScoreFunction       = copy(weight = weight.some)
  def filter(filter: Query): RandomScoreFunction        = copy(filter = filter.some)
}
object RandomScoreFunction {
  def apply(
      seed: Long,
      fieldName: String = SeedAndField.DefaultFieldName,
      weight: Option[Double] = None,
      filter: Option[Query] = None
  ): RandomScoreFunction = new RandomScoreFunction(Some(SeedAndField(seed, fieldName)), weight, filter)
}
