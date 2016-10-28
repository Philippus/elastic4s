package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.SpanQueryBuilder

trait SpanQueryDefinition extends QueryDefinition {
  override def builder: SpanQueryBuilder
}
