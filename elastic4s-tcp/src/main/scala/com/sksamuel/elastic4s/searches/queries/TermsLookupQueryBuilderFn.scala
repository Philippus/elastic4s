package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.queries.term.TermsLookupQueryDefinition
import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

object TermsLookupQueryBuilderFn {
  def apply(q: TermsLookupQueryDefinition): TermsQueryBuilder = {
    val builder = QueryBuilders.termsLookupQuery(q.field, q.termsLookup)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
