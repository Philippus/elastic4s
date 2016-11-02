package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.QueryBuilders

case class SpanFirstQueryDefinition(query: SpanQueryDefinition, end: Int) extends QueryDefinition {
  val builder = QueryBuilders.spanFirstQuery(query.builder, end)
}
