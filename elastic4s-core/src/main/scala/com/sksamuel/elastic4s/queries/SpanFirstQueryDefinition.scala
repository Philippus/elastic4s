package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

case class SpanFirstQueryDefinition(query: SpanQueryDefinition, end: Int) extends QueryDefinition {
  val builder = QueryBuilders.spanFirstQuery(query.builder, end)
}
