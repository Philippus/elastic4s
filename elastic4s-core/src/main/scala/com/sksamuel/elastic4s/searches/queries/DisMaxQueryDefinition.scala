package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class DisMaxQueryDefinition(queries: Seq[QueryDefinition],
                                 boost: Option[Double] = None,
                                 tieBreaker: Option[Double] = None,
                                 queryName: Option[String] = None)
    extends QueryDefinition {

  def boost(boost: Double): DisMaxQueryDefinition           = copy(boost = boost.some)
  def queryName(queryName: String): DisMaxQueryDefinition   = copy(queryName = queryName.some)
  def tieBreaker(tieBreaker: Double): DisMaxQueryDefinition = copy(tieBreaker = tieBreaker.some)
}
