package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.index.query.SpanQueryBuilder

trait SpanQueryDefinition extends QueryDefinition {
  override def builder: SpanQueryBuilder
}
