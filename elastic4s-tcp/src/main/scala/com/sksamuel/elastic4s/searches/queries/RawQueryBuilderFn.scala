package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, WrapperQueryBuilder}

object RawQueryBuilderFn {
  def apply(q: RawQueryDefinition): WrapperQueryBuilder = {
    val builder = QueryBuilders.wrapperQuery(q.json)
    builder
  }
}
