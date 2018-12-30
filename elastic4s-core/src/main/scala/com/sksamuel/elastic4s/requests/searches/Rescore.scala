package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class Rescore(query: Query,
                   windowSize: Option[Int] = None,
                   rescoreQueryWeight: Option[Double] = None,
                   originalQueryWeight: Option[Double] = None,
                   scoreMode: Option[QueryRescoreMode] = None) {

  def window(size: Int): Rescore = copy(windowSize = size.some)

  def originalQueryWeight(weight: Double): Rescore = copy(originalQueryWeight = weight.some)
  def rescoreQueryWeight(weight: Double): Rescore  = copy(rescoreQueryWeight = weight.some)

  def scoreMode(mode: String): Rescore           = scoreMode(QueryRescoreMode.valueOf(mode))
  def scoreMode(mode: QueryRescoreMode): Rescore = copy(scoreMode = mode.some)
}
