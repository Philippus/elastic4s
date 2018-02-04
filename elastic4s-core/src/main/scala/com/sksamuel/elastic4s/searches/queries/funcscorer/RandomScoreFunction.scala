package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class RandomScoreFunction(seed: Long, weight: Option[Double] = None, override val filter: Option[Query] = None)
    extends ScoreFunction {

  def weight(weight: Double): RandomScoreFunction = copy(weight = weight.some)
  def filter(filter: Query): RandomScoreFunction  = copy(filter = filter.some)
}
