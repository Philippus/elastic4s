package com.sksamuel.elastic4s.requests.searches.span

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class SpanWithinQuery(
    little: SpanQuery,
    big: SpanQuery,
    boost: Option[Double] = None,
    queryName: Option[String] = None
) extends SpanQuery {

  def boost(boost: Double): SpanWithinQuery         = copy(boost = boost.some)
  def queryName(queryName: String): SpanWithinQuery = copy(queryName = queryName.some)
}
