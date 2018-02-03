package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class WeightScore(weight: Double, override val filter: Option[Query] = None)
    extends ScoreFunction {
  def filter(filter: Query): ScoreFunction = copy(filter = filter.some)
}
