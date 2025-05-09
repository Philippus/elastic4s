package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.term
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RangeQueryBodyFnTest extends AnyFunSuite with Matchers with GivenWhenThen {

  test("Should correctly build range with relation search query") {
    Given("Some range query")
    val query = RangeQuery(
      field = "timeframe",
      gte = Some("2015-01-01"),
      lte = Some("2016-01-01")
    ).relation(RangeRelation.Within)

    When("Range query is built")
    val queryBody = term.RangeQueryBodyFn(query)

    Then("query should have right field and parameters")
    queryBody.string shouldEqual rangeQueryWithRelation
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
    """.stripMargin.replace(" ", "").replace("\n", "")

}
