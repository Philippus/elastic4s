package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.JsonSugar
import com.sksamuel.elastic4s.requests.searches.queries.ScoreFunctionBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.{FieldValueFactorFunctionModifier, MultiValueMode}
import org.scalatest.FlatSpec

class ScoreFunctionBuilderFnTest extends FlatSpec with JsonSugar {

  import com.sksamuel.elastic4s.ElasticDsl._

  "a score dsl" should "generate correct json for a linear decay function scorer" in {
    val req = linearScore("myfield", "1 2", "2km").offset("200m").decay(0.1).weight(0.1).multiValueMode(MultiValueMode.Min)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_linear.json")
  }

  it should "generate correct json for a gaussian decay function scorer" in {
    val req = gaussianScore("myfield", "1 2", "3km").offset("1km").decay(0.2).weight(0.2).multiValueMode(MultiValueMode.Min)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_gaussian.json")
  }

  it should "generate correct json for an exponential decay function scorer" in {
    val req = exponentialScore("myfield", "1 2", "4km").offset("100m").decay(0.4).weight(0.3).multiValueMode(MultiValueMode.Min)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_exponential.json")
  }

  it should "generate correct json for a random function scorer" in {
    val req = randomScore(12345)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_random.json")
  }

  it should "generate correct json for a script scorer" in {
    val req = scriptScore {
      script("some script").lang("java").param("param1", "value1").params(Map("param2" -> "value2"))
    }.weight(0.4)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_script.json")
  }

  it should "generate correct json for a weight function scorer" in {
    val req = weightScore(1.5)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_weight.json")
  }

  it should "generate correct json for a field value factor function scorer" in {
    val req = fieldFactorScore("likes").factor(1.2).modifier(FieldValueFactorFunctionModifier.SQRT).missing(1.0)
    val builder = ScoreFunctionBuilderFn(req)
    val actual = builder.string()
    actual should matchJsonResource("/score_fieldvaluefactor.json")
  }
}
