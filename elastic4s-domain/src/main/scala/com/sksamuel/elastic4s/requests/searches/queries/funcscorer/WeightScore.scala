package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class WeightScore(weight: Double, override val filter: Option[Query] = None) extends ScoreFunction {
  def filter(filter: Query): ScoreFunction = copy(filter = filter.some)
}
