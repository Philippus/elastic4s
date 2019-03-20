package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.{RangeQuery, RangeRelation}
import org.scalatest.{FunSuite, GivenWhenThen, Matchers}

class RangeQueryBodyFnTest extends FunSuite with Matchers with GivenWhenThen {

  test("Should correctly build range with relation search query") {
    Given("Some range query")
    val query = RangeQuery(
      field = "timeframe",
      gte = Some("2015-01-01"),
      lte = Some("2016-01-01")
    ).relation(RangeRelation.Within)

    When("Range query is built")
    val queryBody = RangeQueryBodyFn(query)

    Then("query should have right field and parameters")
    queryBody.string() shouldEqual rangeQueryWithRelation
  }

  def rangeQueryWithRelation: String =
    """
      |{
      |   "range":{
      |      "timeframe":{
      |         "gte": "2015-01-01",
      |         "lte": "2016-01-01",
      |         "relation": "WITHIN"
      |      }
      |   }
      |}
    """.stripMargin.replaceAllLiterally(" ", "").replace("\n", "")

}
