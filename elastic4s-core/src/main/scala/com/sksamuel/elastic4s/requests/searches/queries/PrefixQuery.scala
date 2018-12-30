package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class PrefixQuery(field: String,
                       prefix: Any,
                       boost: Option[Double] = None,
                       queryName: Option[String] = None,
                       rewrite: Option[String] = None)
    extends MultiTermQuery {

  def queryName(queryName: String): PrefixQuery = copy(queryName = queryName.some)
  def boost(boost: Double): PrefixQuery         = copy(boost = boost.some)
  def rewrite(rewrite: String): PrefixQuery     = copy(rewrite = rewrite.some)
}
