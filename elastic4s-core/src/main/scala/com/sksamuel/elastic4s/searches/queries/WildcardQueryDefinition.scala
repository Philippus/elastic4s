package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class WildcardQueryDefinition(field: String,
                                   query: Any,
                                   boost: Option[Double] = None,
                                   queryName: Option[String] = None,
                                   rewrite: Option[String] = None)
  extends QueryDefinition with MultiTermQueryDefinition {

  def queryName(queryName: String): WildcardQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): WildcardQueryDefinition = copy(boost = boost.some)
  def rewrite(rewrite: String): WildcardQueryDefinition = copy(rewrite = rewrite.some)
}
