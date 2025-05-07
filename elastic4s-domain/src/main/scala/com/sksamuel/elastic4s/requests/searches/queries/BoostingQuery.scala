package com.sksamuel.elastic4s.requests.searches.queries

case class BoostingQuery(
    positiveQuery: Query,
    negativeQuery: Query,
    negativeBoost: Double
) extends Query {
  @deprecated("queryName is not supported in BoostingQuery", "8.18.0")
  def withQueryName(queryName: String): BoostingQuery = this

  @deprecated("boost is not supported in BoostingQuery", "8.18.0")
  def boost(boost: Double): BoostingQuery = this

  @deprecated("Use BoostingQuery with required negativeBoost parameter instead", "8.18.0")
  def negativeBoost(negativeBoost: Double): BoostingQuery = copy(negativeBoost = negativeBoost)

  @deprecated("queryName is not supported in BoostingQuery", "8.18.0")
  def queryName(queryName: String): BoostingQuery = this
}

object BoostingQuery {
  @deprecated("Use BoostingQuery with required negativeBoost parameter instead", "8.18.0")
  def apply(
      positiveQuery: Query,
      negativeQuery: Query
  ): BoostingQuery =
    BoostingQuery(positiveQuery, negativeQuery, 1.0D)
}
