package com.sksamuel.elastic4s.search.queries

import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

case class TermsQueryDefinition(field: String, values: Seq[String]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}
