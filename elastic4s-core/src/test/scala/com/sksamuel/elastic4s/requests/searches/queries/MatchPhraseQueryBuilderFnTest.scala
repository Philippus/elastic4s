package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.text.MatchPhraseQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchPhraseQuery, ZeroTermsQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MatchPhraseQueryBuilderFnTest extends AnyFunSuite with Matchers {
  test("match phrase prefix query builds expected json") {
    val q = MatchPhraseQuery(
      field = "test",
      value = "abc",
      boost = Some(2.0D),
      analyzer = Some("testanalyzer"),
      slop = Some(1),
      queryName = Some("testqueryname"),
      zeroTermsQuery = Some(ZeroTermsQuery.All)
    )
    MatchPhraseQueryBodyFn(q).string shouldBe
      """{"match_phrase":{"test":{"query":"abc","_name":"testqueryname","analyzer":"testanalyzer","slop":1,"boost":2.0,"zero_terms_query":"all"}}}""".stripMargin
  }
}
