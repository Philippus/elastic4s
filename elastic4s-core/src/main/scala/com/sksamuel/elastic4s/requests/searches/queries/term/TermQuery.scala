package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class TermQuery(field: String, value: Any, boost: Option[Double] = None, queryName: Option[String] = None)
    extends Query {

  def boost(boost: Double): TermQuery         = copy(boost = boost.some)
  def queryName(queryName: String): TermQuery = copy(queryName = queryName.some)
}
