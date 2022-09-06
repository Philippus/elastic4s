package com.sksamuel.elastic4s.requests.searches.span

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class SpanContainingQuery(little: SpanQuery,
                               big: SpanQuery,
                               boost: Option[Double] = None,
                               queryName: Option[String] = None)
  extends SpanQuery {

  def boost(boost: Double): SpanContainingQuery = copy(boost = boost.some)
  def queryName(queryName: String): SpanContainingQuery = copy(queryName = queryName.some)
}
