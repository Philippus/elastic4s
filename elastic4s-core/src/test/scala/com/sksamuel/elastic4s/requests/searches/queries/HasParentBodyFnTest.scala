package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.nested.HasParentBodyFn
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class HasParentBodyFnTest extends AnyFunSuite with Matchers {

  test("has parent should generate expected json") {
    val q = HasParentQuery("blog", MatchQuery("tag", "something"), true)
      .boost(1.2)
      .ignoreUnmapped(true)
      .innerHit(InnerHit("inners"))
      .queryName("myquery")
    HasParentBodyFn(q).string() shouldBe
      """{"has_parent":{"parent_type":"blog","query":{"match":{"tag":{"query":"something"}}},"ignore_unmapped":true,"score":true,"boost":1.2,"inner_hits":{"name":"inners"},"_name":"myquery"}}"""
  }
}
