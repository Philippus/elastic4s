package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class WildcardQuery(field: String,
                         query: Any,
                         boost: Option[Double] = None,
                         queryName: Option[String] = None,
                         rewrite: Option[String] = None)
    extends Query
    with MultiTermQuery {

  def queryName(queryName: String): WildcardQuery = copy(queryName = queryName.some)
  def boost(boost: Double): WildcardQuery         = copy(boost = boost.some)
  def rewrite(rewrite: String): WildcardQuery     = copy(rewrite = rewrite.some)
}
