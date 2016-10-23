package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

case class MatchAllQueryDefinition() extends QueryDefinition {

  val builder = QueryBuilders.matchAllQuery

  def boost(boost: Double): MatchAllQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }
}
