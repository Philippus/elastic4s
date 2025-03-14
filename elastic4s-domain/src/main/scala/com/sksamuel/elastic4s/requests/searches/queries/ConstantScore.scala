package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ext.OptionImplicits._

case class ConstantScore(query: Query, boost: Option[Double] = None, queryName: Option[String] = None) extends Query {
  def queryName(queryName: String): ConstantScore = copy(queryName = queryName.some)
  def boost(boost: Double): ConstantScore         = copy(boost = boost.some)
}
