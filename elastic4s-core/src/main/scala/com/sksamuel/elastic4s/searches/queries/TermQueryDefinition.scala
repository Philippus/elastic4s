package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class TermQueryDefinition(field: String,
                               value: Any,
                               boost: Option[Double] = None,
                               queryName: Option[String] = None) extends QueryDefinition {

  def boost(boost: Double): TermQueryDefinition = copy(boost = boost.some)
  def queryName(queryName: String): TermQueryDefinition = copy(queryName = queryName.some)
}
