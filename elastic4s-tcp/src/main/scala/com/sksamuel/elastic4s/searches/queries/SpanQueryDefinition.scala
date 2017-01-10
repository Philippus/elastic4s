package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.SpanQueryBuilder

trait SpanQueryDefinition extends QueryDefinition {
  def builder: SpanQueryBuilder
}
