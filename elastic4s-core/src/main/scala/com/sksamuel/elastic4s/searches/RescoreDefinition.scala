package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class RescoreDefinition(query: QueryDefinition,
                             windowSize: Option[Int] = None,
                             restoreQueryWeight: Option[Double] = None,
                             originalQueryWeight: Option[Double] = None,
                             scoreMode: Option[String] = None) {

  def window(size: Int): RescoreDefinition = copy(windowSize = size.some)

  def originalQueryWeight(weight: Double): RescoreDefinition = copy(originalQueryWeight = weight.some)
  def rescoreQueryWeight(weight: Double): RescoreDefinition = copy(restoreQueryWeight = weight.some)

  def scoreMode(mode: String): RescoreDefinition = copy(scoreMode = mode.some)
}
