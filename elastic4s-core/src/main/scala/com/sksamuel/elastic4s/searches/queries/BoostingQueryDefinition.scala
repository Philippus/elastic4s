package com.sksamuel.elastic4s.searches.queries

case class BoostingQueryDefinition(positiveQuery: QueryDefinition,
                                   negativeQuery: QueryDefinition,
                                   queryName: Option[String] = None,
                                   boost: Option[Double] = None,
                                   negativeBoost: Option[Double] = None)
    extends QueryDefinition {

  def withQueryName(queryName: String): BoostingQueryDefinition = copy(queryName = Option(queryName))

  def boost(boost: Double): BoostingQueryDefinition                 = copy(boost = Option(boost))
  def negativeBoost(negativeBoost: Double): BoostingQueryDefinition = copy(negativeBoost = Option(negativeBoost))
  def queryName(queryName: String): BoostingQueryDefinition         = copy(queryName = Option(queryName))
}
