package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.api.QueryApi
import com.sksamuel.elastic4s.handlers.searches.queries.SparseVectorQueryBuilderFn
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SparseVectorQueryBuilderFnTest extends AnyFunSuite with QueryApi with Matchers with JsonSugar {
  test("Should correctly build minimal sparse vector query") {
    val query = SparseVectorQuery("testfield")

    val queryBody = SparseVectorQueryBuilderFn(query)

    queryBody.string shouldBe """{"sparse_vector":{"field":"testfield"}}"""
  }

  test("Should correctly build sparse vector query using an nlp model") {
    val query = sparseVectorQuery("ml.tokens", "the inference ID to produce the token weights", "the query string")

    val queryBody = SparseVectorQueryBuilderFn(query)

      queryBody.string should matchJson("""{"sparse_vector": {
                                          |   "field":"ml.tokens",
                                          |   "inference_id":"the inference ID to produce the token weights",
                                          |   "query":"the query string"
                                          |}}""".stripMargin.replace("\n", ""))
  }

  test("Should correctly build sparse vector query using precomputed vectors") {
    val query = sparseVectorQuery("ml.tokens", Map("token1" -> 0.5D, "token2" -> 0.3D, "token3" -> 0.2D))

    val queryBody = SparseVectorQueryBuilderFn(query)

    queryBody.string should matchJson("""{"sparse_vector": {
                                        |    "field": "ml.tokens",
                                        |    "query_vector": { "token1": 0.5, "token2": 0.3, "token3": 0.2 }
                                        |}}""".stripMargin.replace("\n", ""))
  }

  test("Should correctly build sparse vector query with pruning configuration") {
    val query = sparseVectorQuery("ml.tokens", "my-elser-model", "How is the weather in Jamaica?")
      .prune(true)
      .pruningConfig(PruningConfig(
        tokensFreqRatioThreshold = Some(5), tokensWeighThreshold = Some(0.4F), onlyScorePrunedTokens = Some(false)
      ))

    val queryBody = SparseVectorQueryBuilderFn(query)

    queryBody.string should matchJson("""{"sparse_vector":{
                                        |    "field": "ml.tokens",
                                        |    "inference_id": "my-elser-model",
                                        |    "query":"How is the weather in Jamaica?",
                                        |    "prune": true,
                                        |    "pruning_config": {
                                        |      "tokens_freq_ratio_threshold": 5,
                                        |      "tokens_weight_threshold": 0.4000000059604645,
                                        |      "only_score_pruned_tokens": false
                                        |    }
                                        |}}""".stripMargin.replace("\n", ""))
  }

  test("Supports boost and queryName") {
    val query = SparseVectorQuery("testfield")
      .boost(1.0D)
      .queryName("abc")

    val queryBody = SparseVectorQueryBuilderFn(query)

    queryBody.string should matchJson("""{"sparse_vector": {
                                        |    "field": "testfield",
                                        |    "boost": 1.0,
                                        |    "_name": "abc"
                                        |}}""".stripMargin.replace("\n", ""))
  }
}
