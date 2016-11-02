package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

case class LongTermsQueryDefinition(field: String, values: Seq[Long]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}
