package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.JsonSugar
import com.sksamuel.elastic4s.handlers.searches.queries.DistanceFeatureQueryBuilderFn
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DistanceFeatureQueryBuilderFnTest extends AnyFunSuite with Matchers with GivenWhenThen with JsonSugar {
  test("Should correctly build distance feature query") {
    Given("A distance feature query")
    val query = DistanceFeatureQuery(field = "production_date", origin = "now", pivot = "7d")

    When("Distance feature query is built")
    val queryBody = DistanceFeatureQueryBuilderFn(query)

    Then("query should have right fields")
    queryBody.string() should matchJson(distanceFeatureQuery)
  }

  val distanceFeatureQuery: String =
    """
      |{
      |    "distance_feature": {
      |        "field": "production_date",
      |        "pivot": "7d",
      |        "origin": "now"
      |    }
      |}
    """.stripMargin
}
