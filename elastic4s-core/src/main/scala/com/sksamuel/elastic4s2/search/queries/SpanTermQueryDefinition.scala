package com.sksamuel.elastic4s2.search.queries

import org.elasticsearch.index.query.QueryBuilders

class SpanTermQueryDefinition(field: String, value: Any) extends SpanQueryDefinition {

  val builder = QueryBuilders.spanTermQuery(field, value.toString)

  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
