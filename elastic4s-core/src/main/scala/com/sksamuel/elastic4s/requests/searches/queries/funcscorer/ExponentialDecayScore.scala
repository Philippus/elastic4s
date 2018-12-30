package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class ExponentialDecayScore(field: String,
                                 origin: String,
                                 scale: String,
                                 offset: Option[Any] = None,
                                 decay: Option[Double] = None,
                                 weight: Option[Double] = None,
                                 multiValueMode: Option[MultiValueMode] = None,
                                 override val filter: Option[Query] = None)
    extends ScoreFunction {

  def decay(decay: Double): ExponentialDecayScore                 = copy(decay = decay.some)
  def offset(offset: Any): ExponentialDecayScore                  = copy(offset = offset.some)
  def weight(weight: Double): ExponentialDecayScore               = copy(weight = weight.some)
  def multiValueMode(mode: MultiValueMode): ExponentialDecayScore = copy(multiValueMode = mode.some)
  def filter(filter: Query): ExponentialDecayScore                = copy(filter = filter.some)
}
