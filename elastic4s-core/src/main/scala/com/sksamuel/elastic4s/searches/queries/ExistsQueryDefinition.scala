package com.sksamuel.elastic4s.searches.queries

case class ExistsQueryDefinition(field: String,
                                 boost: Option[Float] = None,
                                 queryName: Option[String] = None) extends QueryDefinition {

  def withBoost(boost: Float) = copy(boost = Option(boost))
  def withQueryName(queryName: String) = copy(queryName = Option(queryName))
}
