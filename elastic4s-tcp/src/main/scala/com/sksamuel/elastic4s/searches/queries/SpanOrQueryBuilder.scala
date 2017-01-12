package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{QueryBuilders, SpanOrQueryBuilder, SpanQueryBuilder}

object SpanOrQueryBuilder {
  def apply(q: SpanOrQueryDefinition): SpanOrQueryBuilder = {
    val initial = q.clauses.headOption.getOrElse(sys.error("Must have at least one clause"))
    val builder = QueryBuilders.spanOrQuery(QueryBuilderFn(initial).asInstanceOf[SpanQueryBuilder])
    q.clauses.tail.map(QueryBuilderFn.apply).map(_.asInstanceOf[SpanQueryBuilder]).foreach(builder.addClause)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
