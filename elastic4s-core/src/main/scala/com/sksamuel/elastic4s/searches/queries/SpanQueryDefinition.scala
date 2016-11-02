package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.index.query.SpanQueryBuilder

trait SpanQueryDefinition extends QueryDefinition {
  override def builder: SpanQueryBuilder
}
