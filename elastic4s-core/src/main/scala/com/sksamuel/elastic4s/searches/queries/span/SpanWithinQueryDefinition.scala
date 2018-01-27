package com.sksamuel.elastic4s.searches.queries.span

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class SpanWithinQueryDefinition(little: SpanQueryDefinition,
                                     big: SpanQueryDefinition,
                                     boost: Option[Double] = None,
                                     queryName: Option[String] = None)
    extends QueryDefinition {

  def boost(boost: Double): SpanWithinQueryDefinition         = copy(boost = boost.some)
  def queryName(queryName: String): SpanWithinQueryDefinition = copy(queryName = queryName.some)
}
