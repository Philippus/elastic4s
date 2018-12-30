package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.ElasticDsl.matchPhraseQuery
import com.sksamuel.elastic4s.http.JsonSugar
import com.sksamuel.elastic4s.requests.searches.queries.FunctionScoreQueryBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.{CombineFunction, FunctionScoreQuery, FunctionScoreQueryScoreMode, GaussianDecayScore}
import org.scalatest.{FunSuite, Matchers}

class FunctionScoreQueryBuilderFnTest extends FunSuite with Matchers with JsonSugar {

  test("gaussian scorer") {
    val func = FunctionScoreQuery()
      .boost(1.2)
      .functions(GaussianDecayScore("myfield", "now", "28d").offset(19).decay(1.2))
      .minScore(12)
      .scoreMode(FunctionScoreQueryScoreMode.Max)
      .boostMode(CombineFunction.Multiply)

    FunctionScoreQueryBuilderFn(func).string() should matchJsonResource("/gauss_scorer.json")

  }

  test("filter function") {
    val func = FunctionScoreQuery()
      .functions(
        GaussianDecayScore("myfield", "now", "28d").offset(19).decay(1.2).filter(matchPhraseQuery("myfield", "foo"))
      )
    FunctionScoreQueryBuilderFn(func).string() should matchJsonResource("/filter_scorer.json")
  }
}
