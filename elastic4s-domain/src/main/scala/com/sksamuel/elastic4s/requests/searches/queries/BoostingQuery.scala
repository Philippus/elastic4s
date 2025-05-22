package com.sksamuel.elastic4s.requests.searches.queries

case class BoostingQuery(
    positiveQuery: Query,
    negativeQuery: Query,
    negativeBoost: Double
) extends Query
