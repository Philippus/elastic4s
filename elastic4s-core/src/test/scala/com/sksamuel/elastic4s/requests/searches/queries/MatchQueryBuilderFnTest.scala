package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.text.MatchQueryBuilderFn
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchQuery, ZeroTermsQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MatchQueryBuilderFnTest extends AnyFunSuite with Matchers {
  test("match query builds expected json") {
    val q = MatchQuery(
      field = "test",
      value = "abc",
      analyzer = Some("testanalyzer"),
      boost = Some(2.0D),
      cutoffFrequency = Some(0.2D),
      fuzziness = Some("all"),
      fuzzyRewrite = Some("abc"),
      fuzzyTranspositions = Some(true),
      lenient = Some(false),
      maxExpansions = Some(3),
      minimumShouldMatch = Some("1"),
      operator = Some(Operator.And),
      prefixLength = Some(2),
      queryName = Some("testqueryname"),
      autoGenerateSynonymsPhraseQuery = Some(true),
      zeroTermsQuery = Some(ZeroTermsQuery.All)
    )
    MatchQueryBuilderFn(q).string shouldBe
      """{"match":{"test":{"query":"abc","_name":"testqueryname","analyzer":"testanalyzer","auto_generate_synonyms_phrase_query":true,"boost":2.0,"cutoff_frequency":"0.2","fuzziness":"all","fuzzy_transpositions":true,"fuzzy_rewrite":"abc","lenient":"false","max_expansions":3,"minimum_should_match":"1","operator":"AND","prefix_length":"2","zero_terms_query":"all"}}}""".stripMargin
  }
}
