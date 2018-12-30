package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class LinearDecayScore(field: String,
                            origin: String,
                            scale: String,
                            offset: Option[Any] = None,
                            decay: Option[Double] = None,
                            weight: Option[Double] = None,
                            multiValueMode: Option[MultiValueMode] = None,
                            override val filter: Option[Query] = None)
    extends ScoreFunction {

  def multiValueMode(multiValueMode: MultiValueMode): LinearDecayScore =
    copy(multiValueMode = multiValueMode.some)

  def weight(weight: Double): LinearDecayScore = copy(weight = weight.some)
  def decay(decay: Double): LinearDecayScore   = copy(decay = decay.some)
  def offset(offset: Any): LinearDecayScore    = copy(offset = offset.some)
  def filter(filter: Query): LinearDecayScore  = copy(filter = filter.some)
}
