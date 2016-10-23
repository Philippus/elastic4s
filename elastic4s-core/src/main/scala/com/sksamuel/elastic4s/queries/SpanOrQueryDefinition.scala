package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.DefinitionAttributeBoost
import org.elasticsearch.index.query.QueryBuilders

class SpanOrQueryDefinition extends SpanQueryDefinition with DefinitionAttributeBoost {
  val builder = QueryBuilders.spanOrQuery
  val _builder = builder
  def clause(spans: SpanTermQueryDefinition*): SpanOrQueryDefinition = {
    spans.foreach {
      span => builder.clause(span.builder)
    }
    this
  }
}
