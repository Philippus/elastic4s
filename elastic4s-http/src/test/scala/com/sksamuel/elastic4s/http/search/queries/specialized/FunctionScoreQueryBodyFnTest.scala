package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.searches.queries.funcscorer.FunctionScoreQueryDefinition
import org.scalatest.{FunSuite, Matchers}

class FunctionScoreQueryBodyFnTest extends FunSuite with Matchers {

  test("functionScore query should generate expected json with linear decay score and weight") {
    val q = FunctionScoreQueryDefinition().query(
      boolQuery()
        .must(matchAllQuery())
        .filter(termQuery("flags", "ANYSTRING"))
    ).scorers(
      Seq(
        linearScore(
          "publishedDate",
          origin = "now",
          scale = "20d"
        ).decay(0.5).weight(1.15),
        fieldFactorScore("boost")
      )
    )
    FunctionScoreQueryBodyFn(q).string() shouldBe
      """{"function_score":{"query":{"bool":{"must":[{"match_all":{}}],"filter":[{"term":{"flags":{"value":"ANYSTRING"}}}]}},"functions":[{"linear":{"publishedDate":{"origin":"now","scale":"20d","decay":0.5}},"weight":1.15},{"field_value_factor":{"field":"boost","modifier":"none"}}]}}"""
  }

  test("functionScore query should generate expected json with gaussian decay score") {
    val q = FunctionScoreQueryDefinition().query(
      boolQuery()
        .must(matchAllQuery())
        .filter(termQuery("flags", "ANYSTRING"))
    ).scorers(
      Seq(
        gaussianScore(
          "publishedDate",
          origin = "now",
          scale = "20d"
        ).decay(0.5),
        fieldFactorScore("boost")
      )
    )
    FunctionScoreQueryBodyFn(q).string() shouldBe
      """{"function_score":{"query":{"bool":{"must":[{"match_all":{}}],"filter":[{"term":{"flags":{"value":"ANYSTRING"}}}]}},"functions":[{"gaussian":{"publishedDate":{"origin":"now","scale":"20d","decay":0.5}}},{"field_value_factor":{"field":"boost","modifier":"none"}}]}}"""
  }

  test("functionScore query should generate expected json with exponential decay score") {
    val q = FunctionScoreQueryDefinition().query(
      boolQuery()
        .must(matchAllQuery())
        .filter(termQuery("flags", "ANYSTRING"))
    ).scorers(
      Seq(
        exponentialScore(
          "publishedDate",
          origin = "now",
          scale = "20d"
        ).decay(0.5),
        fieldFactorScore("boost")
      )
    )
    FunctionScoreQueryBodyFn(q).string() shouldBe
      """{"function_score":{"query":{"bool":{"must":[{"match_all":{}}],"filter":[{"term":{"flags":{"value":"ANYSTRING"}}}]}},"functions":[{"exp":{"publishedDate":{"origin":"now","scale":"20d","decay":0.5}}},{"field_value_factor":{"field":"boost","modifier":"none"}}]}}"""
  }

  test("functionScore query should generate expected json with random score") {
    val q = FunctionScoreQueryDefinition().query(
      boolQuery()
        .must(matchAllQuery())
        .filter(termQuery("flags", "ANYSTRING"))
    ).scorers(
      Seq(
        randomScore(23456789),
        fieldFactorScore("boost")
      )
    )
    FunctionScoreQueryBodyFn(q).string() shouldBe
      """{"function_score":{"query":{"bool":{"must":[{"match_all":{}}],"filter":[{"term":{"flags":{"value":"ANYSTRING"}}}]}},"functions":[{"random_score":{"seed":23456789}},{"field_value_factor":{"field":"boost","modifier":"none"}}]}}"""
  }

  test("functionScore query should generate expected json with script score") {
    val params: Map[String, Double] = Map("a" -> 5, "b" -> 1.2)
    val q = FunctionScoreQueryDefinition().query(
      boolQuery()
        .must(matchAllQuery())
        .filter(termQuery("flags", "ANYSTRING"))
    ).scorers(
      Seq(
        scriptScore(
          script("params.a / Math.pow(params.b, doc['likes'].value)")
            .params(params)
        ),
        fieldFactorScore("boost")
      )
    )
    FunctionScoreQueryBodyFn(q).string() shouldBe
      """{"function_score":{"query":{"bool":{"must":[{"match_all":{}}],"filter":[{"term":{"flags":{"value":"ANYSTRING"}}}]}},"functions":[{"script_score":{"script":{"inline":"params.a / Math.pow(params.b, doc['likes'].value)","params":{"a":5.0,"b":1.2}}}},{"field_value_factor":{"field":"boost","modifier":"none"}}]}}"""
  }

  test("functionScore query should generate expected json with weight score") {
    val q = FunctionScoreQueryDefinition().query(
      boolQuery()
        .must(matchAllQuery())
        .filter(termQuery("flags", "ANYSTRING"))
    ).scorers(
      Seq(weightScore(1.2))
    )
    FunctionScoreQueryBodyFn(q).string() shouldBe
      """{"function_score":{"query":{"bool":{"must":[{"match_all":{}}],"filter":[{"term":{"flags":{"value":"ANYSTRING"}}}]}},"functions":[{"weight":1.2}]}}"""
  }
}
