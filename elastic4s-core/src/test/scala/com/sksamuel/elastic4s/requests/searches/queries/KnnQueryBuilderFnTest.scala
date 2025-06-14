package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.KnnQueryBuilderFn
import com.sksamuel.elastic4s.requests.searches.knn.Knn
import com.sksamuel.elastic4s.requests.searches.term.TermQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class KnnQueryBuilderFnTest extends AnyFunSuite with Matchers {
  test("Knn generates proper query") {
    val request = Knn("image-vector") numCandidates 50 queryVector Seq(54D, 10D, -2D) k 5 filter TermQuery(
      "file-type",
      "png"
    ) similarity 10 boost .4
    KnnQueryBuilderFn(request).string shouldBe
      """{"knn":{"field":"image-vector","filter":{"term":{"file-type":{"value":"png"}}},"k":5,"num_candidates":50,"query_vector":[54.0,10.0,-2.0],"similarity":10.0,"boost":0.4}}""".stripMargin
  }
}
