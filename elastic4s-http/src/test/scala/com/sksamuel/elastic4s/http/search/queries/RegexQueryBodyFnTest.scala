package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.search.queries.term.RegexQueryBodyFn
import com.sksamuel.elastic4s.searches.queries.RegexQueryDefinition
import org.elasticsearch.index.query.RegexpFlag
import org.scalatest.{FunSuite, Matchers}

class RegexQueryBodyFnTest extends FunSuite with Matchers {

  test("regex query should generate expected json") {
    val q = RegexQueryDefinition("mysearch", ".*")
      .flags(RegexpFlag.ANYSTRING, RegexpFlag.COMPLEMENT, RegexpFlag.EMPTY, RegexpFlag.INTERSECTION, RegexpFlag.INTERVAL)
      .boost(1.2)
      .queryName("myquery")
      .maxDeterminedStates(10000)
    RegexQueryBodyFn(q).string() shouldBe
      """{"regexp":{"mysearch":{"value":".*","flags":"ANYSTRING|COMPLEMENT|EMPTY|INTERSECTION|INTERVAL","max_determinized_states":10000,"boost":1.2,"_name":"myquery"}}}"""
  }
}
