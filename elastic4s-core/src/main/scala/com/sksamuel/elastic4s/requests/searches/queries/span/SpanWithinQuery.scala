package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class SpanWithinQuery(little: SpanQuery,
                           big: SpanQuery,
                           boost: Option[Double] = None,
                           queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): SpanWithinQuery         = copy(boost = boost.some)
  def queryName(queryName: String): SpanWithinQuery = copy(queryName = queryName.some)
}
