package com.sksamuel.elastic4s.searches.queries.span

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{MultiTermQueryBuilder, QueryBuilders, SpanMultiTermQueryBuilder}

object SpanMultiTermQueryBuilder {
  def apply(q: SpanMultiTermQueryDefinition): SpanMultiTermQueryBuilder = {
    val builder = QueryBuilders.spanMultiTermQueryBuilder(QueryBuilderFn(q.query).asInstanceOf[MultiTermQueryBuilder])
    builder
  }
}
