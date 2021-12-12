package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.term
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PrefixQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("prefix query should generate expected json") {
    val q = PrefixQuery("mysearch", "starts")
      .boost(1.2)
      .queryName("myquery")
      .caseInsensitive(true)
    term.PrefixQueryBodyFn(q).string() shouldBe
      """{"prefix":{"mysearch":{"value":"starts","boost":1.2,"_name":"myquery","case_insensitive":true}}}"""
  }
}
