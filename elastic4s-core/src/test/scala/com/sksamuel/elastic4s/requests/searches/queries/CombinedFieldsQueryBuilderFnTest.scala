package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.text.CombinedFieldsQueryBodyFn
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.matches.ZeroTermsQuery.All
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CombinedFieldsQueryBuilderFnTest extends AnyFunSuite with Matchers {
  test("combined fields query builds expected json") {
    val q = CombinedFieldsQuery(
      query = "test",
      fields = Seq(("a", None), ("b", Some(0.3D))),
      autoGenerateSynonymsPhraseQuery = Some(false),
      operator = Some(Operator.And),
      minimumShouldMatch = Some("3"),
      zeroTermsQuery = Some(All)
    )
    CombinedFieldsQueryBodyFn(q).string shouldBe
      """{"combined_fields":{"query":"test","fields":["a","b^0.3"],"auto_generate_synonyms_phrase_query":false,"operator":"And","minimum_should_match":"3","zero_terms_query":"all"}}""".stripMargin
  }
}
