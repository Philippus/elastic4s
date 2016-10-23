package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

class SpanNearQueryDefinition extends SpanQueryDefinition {

  val builder = QueryBuilders.spanNearQuery()

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def inOrder(inOrder: Boolean): this.type = {
    builder.inOrder(inOrder)
    this
  }

  def collectPayloads(collectPayloads: Boolean): this.type = {
    builder.collectPayloads(collectPayloads)
    this
  }

  def clause(query: SpanQueryDefinition): this.type = {
    builder.clause(query.builder)
    this
  }

  def slop(slop: Int): this.type = {
    builder.slop(slop)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
