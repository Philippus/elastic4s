package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, TermQueryBuilder}

object TermQueryBuilder {
  def apply(q: TermQueryDefinition): TermQueryBuilder = {
    val builder = q.value match {
      case str: String => QueryBuilders.termQuery(q.field, str)
      case iter: Iterable[Any] => QueryBuilders.termQuery(q.field, iter.toArray)
      case other => QueryBuilders.termQuery(q.field, other)
    }
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
