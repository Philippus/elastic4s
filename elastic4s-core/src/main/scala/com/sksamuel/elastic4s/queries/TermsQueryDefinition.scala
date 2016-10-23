package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

case class TermsQueryDefinition(field: String, values: Seq[Any]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}
