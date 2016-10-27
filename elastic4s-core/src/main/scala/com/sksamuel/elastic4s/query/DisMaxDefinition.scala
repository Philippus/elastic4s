package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

class DisMaxDefinition(queries: Seq[QueryDefinition]) extends QueryDefinition {

  def builder = {
    val builder = QueryBuilders.disMaxQuery()
    queries.foreach(q => builder.add(q.builder))
    builder
  }

  def boost(b: Double): DisMaxDefinition = {
    builder.boost(b.toFloat)
    this
  }

  def tieBreaker(tieBreaker: Double): DisMaxDefinition = {
    builder.tieBreaker(tieBreaker.toFloat)
    this
  }

  def queryName(queryName: String): DisMaxDefinition = {
    builder.queryName(queryName)
    this
  }
}
