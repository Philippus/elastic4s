package com.sksamuel.elastic4s.requests.searches.queries

case class BoostingQuery(positiveQuery: Query,
                         negativeQuery: Query,
                         queryName: Option[String] = None,
                         boost: Option[Double] = None,
                         negativeBoost: Option[Double] = None)
    extends Query {

  def withQueryName(queryName: String): BoostingQuery = copy(queryName = Option(queryName))

  def boost(boost: Double): BoostingQuery                 = copy(boost = Option(boost))
  def negativeBoost(negativeBoost: Double): BoostingQuery = copy(negativeBoost = Option(negativeBoost))
  def queryName(queryName: String): BoostingQuery         = copy(queryName = Option(queryName))
}
