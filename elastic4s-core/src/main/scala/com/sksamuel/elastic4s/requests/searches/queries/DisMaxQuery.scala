package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class DisMaxQuery(queries: Seq[Query],
                       boost: Option[Double] = None,
                       tieBreaker: Option[Double] = None,
                       queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): DisMaxQuery           = copy(boost = boost.some)
  def queryName(queryName: String): DisMaxQuery   = copy(queryName = queryName.some)
  def tieBreaker(tieBreaker: Double): DisMaxQuery = copy(tieBreaker = tieBreaker.some)
}
