package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.PinnedQueryBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchAllQuery
import org.scalatest.GivenWhenThen
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PinnedQueryBodyFnTest extends AnyFunSuite with Matchers with GivenWhenThen {

  test("Should correctly build pinned query") {
    Given("Some pinned query")
    val query = PinnedQuery(
      ids = List("1", "2", "3"),
      organic = MatchAllQuery()
    )

    When("Pinned query is built")
    val queryBody = PinnedQueryBuilderFn(query)

    Then("query should have right fields")
    queryBody.string shouldEqual pinnedQuery
  }

  def pinnedQuery: String =
    """
      |{
      |   "pinned":{
      |      "ids": ["1","2","3"],
      |      "organic": {
      |         "match_all": {}
      |      }
      |   }
      |}
    """.stripMargin.replace(" ", "").replace("\n", "")

}
