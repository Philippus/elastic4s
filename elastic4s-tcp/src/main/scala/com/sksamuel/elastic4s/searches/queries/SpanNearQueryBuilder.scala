package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{QueryBuilders, SpanNearQueryBuilder, SpanQueryBuilder}

object SpanNearQueryBuilder {
  def apply(q: SpanNearQueryDefinition): SpanNearQueryBuilder = {
    val initial = q.clauses.headOption.getOrElse(sys.error("Must have at least one clause"))
    val builder = QueryBuilders.spanNearQuery(QueryBuilderFn(initial).asInstanceOf[SpanQueryBuilder], q.slop)
    q.clauses.tail.map(QueryBuilderFn.apply).map(_.asInstanceOf[SpanQueryBuilder]).foreach(builder.addClause)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.inOrder.foreach(builder.inOrder)
    builder
  }
}
