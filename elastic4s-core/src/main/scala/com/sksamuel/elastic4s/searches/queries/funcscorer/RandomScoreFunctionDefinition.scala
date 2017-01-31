package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.exts.OptionImplicits._

case class RandomScoreFunctionDefinition(seed: Long,
                                         weight: Option[Double] = None) extends ScoreFunctionDefinition {

  def weight(weight: Double): RandomScoreFunctionDefinition = copy(weight = weight.some)
}
