package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries.{QueryStringBuilder, QueryStringQueryDefinition}
import org.elasticsearch.index.query.QueryBuilder

object QueryBuilderFn {
  def apply(query: QueryDefinition): QueryBuilder = query match {
    case qs: QueryStringQueryDefinition => QueryStringBuilder.builder(qs)
  }
}
