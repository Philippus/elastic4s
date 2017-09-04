package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class RandomScoreFunctionDefinition(seed: Long,
                                         weight: Option[Double] = None,
                                         override val filter: Option[QueryDefinition] = None) extends ScoreFunctionDefinition {

  def weight(weight: Double): RandomScoreFunctionDefinition = copy(weight = weight.some)
  def filter(filter: QueryDefinition): RandomScoreFunctionDefinition = copy(filter = filter.some)
}
