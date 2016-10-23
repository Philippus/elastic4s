package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

class BoostingQueryDefinition extends QueryDefinition {

  val builder = QueryBuilders.boostingQuery()

  def positive(block: => QueryDefinition) = {
    builder.positive(block.builder)
    this
  }

  def negative(block: => QueryDefinition) = {
    builder.negative(block.builder)
    this
  }

  def positiveBoost(b: Double) = {
    builder.boost(b.toFloat)
    this
  }

  def negativeBoost(b: Double) = {
    builder.negativeBoost(b.toFloat)
    this
  }
}
