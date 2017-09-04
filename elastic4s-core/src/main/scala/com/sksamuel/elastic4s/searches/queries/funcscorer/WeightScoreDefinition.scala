package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class WeightScoreDefinition(weight: Double,
                                 override val filter: Option[QueryDefinition] = None) extends ScoreFunctionDefinition {
  def filter(filter: QueryDefinition): ScoreFunctionDefinition = copy(filter = filter.some)
}
