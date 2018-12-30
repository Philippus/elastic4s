package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.requests.searches.queries.term.RegexQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.queries.{RegexQuery, RegexpFlag}
import org.scalatest.{FunSuite, Matchers}

class RegexQueryBodyFnTest extends FunSuite with Matchers {

  test("regex query should generate expected json") {
    val q = RegexQuery("mysearch", ".*")
      .flags(RegexpFlag.AnyString, RegexpFlag.Complement, RegexpFlag.Empty, RegexpFlag.Intersection, RegexpFlag.Interval)
      .boost(1.2)
      .queryName("myquery")
      .maxDeterminedStates(10000)
    RegexQueryBodyFn(q).string() shouldBe
      """{"regexp":{"mysearch":{"value":".*","flags":"ANYSTRING|COMPLEMENT|EMPTY|INTERSECTION|INTERVAL","max_determinized_states":10000,"boost":1.2,"_name":"myquery"}}}"""
  }
}
