package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.GenericTermsQueryDefinition
import org.elasticsearch.index.query.{QueryBuilders, TermsQueryBuilder}

case class DoubleTermsQueryDefinition(field: String, values: Seq[Double]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}
