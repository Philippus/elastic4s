package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches
import com.sksamuel.elastic4s.handlers.searches.queries.RankFeatureQueryBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.RankFeatureQuery.{Log, Saturation, Sigmoid}
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RankFeatureQueryBodyFnTest extends AnyFunSuite with Matchers with GivenWhenThen {
  test("Should correctly build rank feature query with a sigmoid function") {
    Given("A rank feature query with a sigmoid function")
    val query = RankFeatureQuery(field = "pagerank").boost(0.5).withSigmoid(Sigmoid(7, 0.6))

    When("Rank feature query is built")
    val queryBody = RankFeatureQueryBuilderFn(query)

    Then("query should have right fields")
    queryBody.string() shouldEqual rankFeatureQueryWithSigmoid
  }

  def rankFeatureQueryWithSigmoid: String =
    """
      |{
      |   "rank_feature":{
      |      "field": "pagerank",
      |      "boost": 0.5,
      |      "sigmoid": {
      |         "pivot": 7,
      |         "exponent": 0.6
      |      }
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

  test("Should correctly build a rank feature query with a log function") {
    {
      Given("A rank feature query with a log function")
      val query = RankFeatureQuery(field = "pagerank").withLog(Log(3))

      When("Rank feature query is built")
      val queryBody = searches.queries.RankFeatureQueryBuilderFn(query)

      Then("query should have right fields")
      queryBody.string() shouldEqual rankFeatureQueryWithLog
    }

    def rankFeatureQueryWithLog: String =
      """
        |{
        |   "rank_feature":{
        |      "field": "pagerank",
        |      "log": {
        |          "scaling_factor": 3
        |      }
        |   }
        |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")
  }

  test("Should correctly build a rank feature query with a saturation function") {
    {
      Given("A rank feature query with a saturation function")
      val query = RankFeatureQuery("pagerank").withSaturation(Saturation(Some(2)))

      When("Rank feature query is built")
      val queryBody = searches.queries.RankFeatureQueryBuilderFn(query)

      Then("query should have right fields")
      queryBody.string() shouldEqual rankFeatureQueryWithSaturation
    }

    def rankFeatureQueryWithSaturation: String =
      """
        |{
        |   "rank_feature":{
        |      "field": "pagerank",
        |      "saturation": {
        |          "pivot": 2
        |      }
        |   }
        |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

    Given("A rank feature query with a saturation function without specified pivot")
    val query = RankFeatureQuery("pagerank").withSaturation(Saturation(None))

    When("Rank feature query is built")
    val queryBody = searches.queries.RankFeatureQueryBuilderFn(query)

    Then("query should have right fields")
    queryBody.string() shouldEqual minimalRankFeatureQuery
  }

  def minimalRankFeatureQuery: String =
    """
      |{
      |   "rank_feature":{
      |      "field": "pagerank",
      |      "saturation": {}
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")
}
