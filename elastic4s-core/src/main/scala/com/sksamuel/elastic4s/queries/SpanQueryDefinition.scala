package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.SpanQueryBuilder

trait SpanQueryDefinition extends QueryDefinition {
  override def builder: SpanQueryBuilder
}
