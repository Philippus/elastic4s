package com.sksamuel.elastic4s.search.query

import org.elasticsearch.index.query.QueryBuilders

case class SpanMultiTermQueryDefinition(query: MultiTermQueryDefinition) extends SpanQueryDefinition {
  override val builder = QueryBuilders.spanMultiTermQueryBuilder(query.builder)
}
