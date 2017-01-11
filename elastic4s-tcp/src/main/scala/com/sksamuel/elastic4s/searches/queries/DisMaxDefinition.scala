package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class DisMaxDefinition(queries: Seq[QueryDefinition],
                            boost: Option[Double] = None,
                            tieBreaker: Option[Double] = None,
                            queryName: Option[String] = None
                           ) extends QueryDefinition {

  def boost(boost: Double): DisMaxDefinition = copy(boost = boost.some)
  def queryName(queryName: String): DisMaxDefinition = copy(queryName = queryName.some)
  def tieBreaker(tieBreaker: Double): DisMaxDefinition = copy(tieBreaker = tieBreaker.some)
}
