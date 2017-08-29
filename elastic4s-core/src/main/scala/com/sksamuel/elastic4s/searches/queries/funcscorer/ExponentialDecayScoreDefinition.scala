package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class ExponentialDecayScoreDefinition(field: String,
                                           origin: String,
                                           scale: String,
                                           offset: Option[Any] = None,
                                           decay: Option[Double] = None,
                                           weight: Option[Double] = None,
                                           multiValueMode: Option[MultiValueMode] = None,
                                           override val filter: Option[QueryDefinition] = None)
  extends ScoreFunctionDefinition {

  def decay(decay: Double): ExponentialDecayScoreDefinition = copy(decay = decay.some)
  def offset(offset: Any): ExponentialDecayScoreDefinition = copy(offset = offset.some)
  def weight(weight: Double): ExponentialDecayScoreDefinition = copy(weight = weight.some)
  def multiValueMode(mode: MultiValueMode): ExponentialDecayScoreDefinition = copy(multiValueMode = mode.some)
  def filter(filter: QueryDefinition): ExponentialDecayScoreDefinition = copy(filter = filter.some)
}
