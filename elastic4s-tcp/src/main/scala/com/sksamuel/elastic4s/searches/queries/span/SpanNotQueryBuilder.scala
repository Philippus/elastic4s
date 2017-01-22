package com.sksamuel.elastic4s.searches.queries.span

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{QueryBuilders, SpanNotQueryBuilder, SpanQueryBuilder}

object SpanNotQueryBuilder {
  def apply(q: SpanNotQueryDefinition): SpanNotQueryBuilder = {
    val builder = QueryBuilders.spanNotQuery(
      QueryBuilderFn(q.include).asInstanceOf[SpanQueryBuilder],
      QueryBuilderFn(q.exclude).asInstanceOf[SpanQueryBuilder]
    )
    q.dist.foreach(builder.dist)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.pre.foreach(builder.pre)
    q.post.foreach(builder.post)
    builder
  }
}
