package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class ConstantScoreDefinition(query: QueryDefinition,
                                   boost: Option[Double] = None,
                                   queryName: Option[String] = None) extends QueryDefinition {
  def queryName(queryName: String): ConstantScoreDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): ConstantScoreDefinition = copy(boost = boost.some)
}
