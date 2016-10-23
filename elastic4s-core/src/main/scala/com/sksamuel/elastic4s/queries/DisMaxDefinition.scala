package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

class DisMaxDefinition extends QueryDefinition {
  val builder = QueryBuilders.disMaxQuery()

  def query(queries: QueryDefinition*): DisMaxDefinition = {
    queries.foreach(q => builder.add(q.builder))
    this
  }

  def queryName(queryName: String): DisMaxDefinition = {
    builder.queryName(queryName)
    this
  }

  def boost(b: Double): DisMaxDefinition = {
    builder.boost(b.toFloat)
    this
  }

  def tieBreaker(tieBreaker: Double): DisMaxDefinition = {
    builder.tieBreaker(tieBreaker.toFloat)
    this
  }
}
