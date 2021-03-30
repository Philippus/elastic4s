package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ElasticDsl.matchPhraseQuery
import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.handlers.searches.queries.FunctionScoreQueryBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.{CombineFunction, FunctionScoreQuery, FunctionScoreQueryScoreMode, GaussianDecayScore}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FunctionScoreQueryBuilderFnTest extends AnyFunSuite with Matchers with JsonSugar {

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
    queries.FunctionScoreQueryBuilderFn(func).string() should matchJsonResource("/filter_scorer.json")
  }
}
