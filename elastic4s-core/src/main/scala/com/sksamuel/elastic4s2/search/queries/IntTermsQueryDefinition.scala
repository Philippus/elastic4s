package com.sksamuel.elastic4s2.search.queries

import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

case class IntTermsQueryDefinition(field: String, values: Seq[Int]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}
