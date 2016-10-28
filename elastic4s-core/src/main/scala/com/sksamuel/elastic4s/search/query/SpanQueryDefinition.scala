package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.SpanQueryBuilder

trait SpanQueryDefinition extends QueryDefinition {
  override def builder: SpanQueryBuilder
}
