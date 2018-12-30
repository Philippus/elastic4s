package com.sksamuel.elastic4s.requests.searches.queries

case class ExistsQuery(field: String, boost: Option[Double] = None, queryName: Option[String] = None) extends Query {

  def boost(boost: Double): ExistsQuery     = copy(boost = Option(boost))
  def withBoost(boost: Double): ExistsQuery = copy(boost = Option(boost))

  def queryName(queryName: String): ExistsQuery     = copy(queryName = Option(queryName))
  def withQueryName(queryName: String): ExistsQuery = copy(queryName = Option(queryName))
}
