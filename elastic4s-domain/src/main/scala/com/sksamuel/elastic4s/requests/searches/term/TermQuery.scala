package com.sksamuel.elastic4s.requests.searches.term

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class TermQuery(
    field: String,
    value: Any,
    boost: Option[Double] = None,
    queryName: Option[String] = None,
    caseInsensitive: Option[Boolean] = None
) extends Query {

  def boost(boost: Double): TermQuery                      = copy(boost = boost.some)
  def queryName(queryName: String): TermQuery              = copy(queryName = queryName.some)
  def caseInsensitive(caseInsensitive: Boolean): TermQuery = copy(caseInsensitive = caseInsensitive.some)
}
