package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.term
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RegexQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("regex query should generate expected json") {
    val q = RegexQuery("mysearch", ".*")
      .flags(
        RegexpFlag.AnyString,
        RegexpFlag.Complement,
        RegexpFlag.Empty,
        RegexpFlag.Intersection,
        RegexpFlag.Interval
      )
      .boost(1.2)
      .queryName("myquery")
      .maxDeterminedStates(10000)
      .caseInsensitive(true)
    term.RegexQueryBodyFn(q).string shouldBe
      """{"regexp":{"mysearch":{"value":".*","flags":"ANYSTRING|COMPLEMENT|EMPTY|INTERSECTION|INTERVAL","max_determinized_states":10000,"boost":1.2,"_name":"myquery","case_insensitive":true}}}"""
  }
}
