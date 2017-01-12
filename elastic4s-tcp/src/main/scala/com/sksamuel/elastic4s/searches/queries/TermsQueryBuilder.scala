package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.TermsQueryBuilder

object TermsQueryBuilder {
  def apply[T](q: TermsQueryDefinition[T]): TermsQueryBuilder = {
    val builder = q.buildable.build(q).asInstanceOf[TermsQueryBuilder]
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    builder
  }
}
