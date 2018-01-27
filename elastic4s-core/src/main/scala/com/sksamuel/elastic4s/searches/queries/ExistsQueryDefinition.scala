package com.sksamuel.elastic4s.searches.queries

case class ExistsQueryDefinition(field: String, boost: Option[Double] = None, queryName: Option[String] = None)
    extends QueryDefinition {

  def boost(boost: Double): ExistsQueryDefinition     = copy(boost = Option(boost))
  def withBoost(boost: Double): ExistsQueryDefinition = copy(boost = Option(boost))

  def queryName(queryName: String): ExistsQueryDefinition     = copy(queryName = Option(queryName))
  def withQueryName(queryName: String): ExistsQueryDefinition = copy(queryName = Option(queryName))
}
