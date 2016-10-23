package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

case class ExistsQueryDefinition(field: String) extends QueryDefinition {

  val builder = QueryBuilders.existsQuery(field)

  def queryName(name: String): ExistsQueryDefinition = {
    builder.queryName(name)
    this
  }
}
