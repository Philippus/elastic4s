package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.term
import com.sksamuel.elastic4s.requests.searches.term.TermQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TermQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("term query should generate expected json") {
    val q = TermQuery("mysearch", "myvalue")
      .boost(1.2)
      .queryName("myquery")
      .caseInsensitive(true)
    term.TermQueryBodyFn(q).string() shouldBe
      """{"term":{"mysearch":{"boost":1.2,"_name":"myquery","value":"myvalue","case_insensitive":true}}}"""
  }
}
