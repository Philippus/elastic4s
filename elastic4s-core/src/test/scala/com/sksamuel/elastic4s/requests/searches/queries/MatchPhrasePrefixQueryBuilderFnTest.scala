package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.text.MatchPhrasePrefixBodyFn
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchPhrasePrefixQuery, ZeroTermsQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MatchPhrasePrefixQueryBuilderFnTest extends AnyFunSuite with Matchers {
  test("match phrase prefix query builds expected json") {
    val q = MatchPhrasePrefixQuery(
      field = "test",
      value = "abc",
      analyzer = Some("testanalyzer"),
      queryName = Some("testqueryname"),
      boost = Some(2.0D),
      maxExpansions = Some(3),
      slop = Some(1),
      zeroTermsQuery = Some(ZeroTermsQuery.All)
    )
    MatchPhrasePrefixBodyFn(q).string shouldBe
      """{"match_phrase_prefix":{"test":{"query":"abc","_name":"testqueryname","analyzer":"testanalyzer","slop":1,"max_expansions":3,"boost":2.0,"zero_terms_query":"all"}}}""".stripMargin
  }
}
