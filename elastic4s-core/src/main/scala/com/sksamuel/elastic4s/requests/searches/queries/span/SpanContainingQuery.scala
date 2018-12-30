package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class SpanContainingQuery(little: SpanQuery,
                               big: SpanQuery,
                               boost: Option[Double] = None,
                               queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): SpanContainingQuery         = copy(boost = boost.some)
  def queryName(queryName: String): SpanContainingQuery = copy(queryName = queryName.some)
}
