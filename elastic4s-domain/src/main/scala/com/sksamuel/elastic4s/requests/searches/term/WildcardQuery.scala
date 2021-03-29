package com.sksamuel.elastic4s.requests.searches.term

import com.sksamuel.elastic4s.requests.searches.queries.{MultiTermQuery, Query}

case class WildcardQuery(field: String,
                         query: Any,
                         boost: Option[Double] = None,
                         queryName: Option[String] = None,
                         rewrite: Option[String] = None,
                         caseInsensitive: Option[Boolean] = None)
  extends Query
    with MultiTermQuery {

  def queryName(queryName: String): WildcardQuery = copy(queryName = Option(queryName))
  def boost(boost: Double): WildcardQuery = copy(boost = Option(boost))
  def rewrite(rewrite: String): WildcardQuery = copy(rewrite = Option(rewrite))
  def caseInsensitive(caseInsensitive: Boolean): WildcardQuery = copy(caseInsensitive = Option(caseInsensitive))
}
