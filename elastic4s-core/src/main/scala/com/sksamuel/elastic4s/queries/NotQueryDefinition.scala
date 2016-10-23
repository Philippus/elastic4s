package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

@deprecated("use bool query with a mustNot clause", "2.1.1")
class NotQueryDefinition(filter: QueryDefinition)
  extends QueryDefinition {

  val builder = QueryBuilders.notQuery(filter.builder)
  val _builder = builder

  def queryName(queryName: String): NotQueryDefinition = {
    builder.queryName(queryName)
    this
  }
}
