package com.sksamuel.elastic4s.searches.queries.span

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{QueryBuilders, SpanFirstQueryBuilder, SpanQueryBuilder}

object SpanFirstQueryBuilder {
  def apply(q: SpanFirstQueryDefinition): SpanFirstQueryBuilder = {
    val builder = QueryBuilders.spanFirstQuery(QueryBuilderFn(q).asInstanceOf[SpanQueryBuilder], q.end)
    builder
  }
}
