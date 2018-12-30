package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class GaussianDecayScore(field: String,
                              origin: String,
                              scale: String,
                              offset: Option[Any] = None,
                              decay: Option[Double] = None,
                              weight: Option[Double] = None,
                              multiValueMode: Option[MultiValueMode] = None,
                              override val filter: Option[Query] = None)
    extends ScoreFunction {

  def multiValueMode(multiValueMode: MultiValueMode): GaussianDecayScore =
    copy(multiValueMode = multiValueMode.some)

  def weight(weight: Double): GaussianDecayScore = copy(weight = weight.some)
  def decay(decay: Double): GaussianDecayScore   = copy(decay = decay.some)
  def offset(offset: Any): GaussianDecayScore    = copy(offset = offset.some)
  def filter(filter: Query): GaussianDecayScore  = copy(filter = filter.some)
}
