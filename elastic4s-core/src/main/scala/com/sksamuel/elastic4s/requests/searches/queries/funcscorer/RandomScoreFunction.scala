package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class RandomScoreFunction(seed: Long, fieldName: String = "_seq_no", weight: Option[Double] = None, override val filter: Option[Query] = None)
    extends ScoreFunction {

  def fieldName(fieldName: String): RandomScoreFunction = copy(fieldName = fieldName)
  def weight(weight: Double): RandomScoreFunction = copy(weight = weight.some)
  def filter(filter: Query): RandomScoreFunction  = copy(filter = filter.some)
}
