package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.queries.term.TermsLookupQueryDefinition
import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

object TermsLookupQueryBuilderFn {
  def apply(q: TermsLookupQueryDefinition): TermsQueryBuilder = {
    val lookup = new org.elasticsearch.indices.TermsLookup(q.termsLookup.ref.index,
                                                           q.termsLookup.ref.`type`,
                                                           q.termsLookup.ref.id,
                                                           q.termsLookup.path)
    q.termsLookup.routing.foreach(lookup.routing)
    val builder = QueryBuilders.termsLookupQuery(q.field, lookup)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
