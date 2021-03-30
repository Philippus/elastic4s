package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.nested.HasChildBodyFn
import com.sksamuel.elastic4s.requests.searches.ScoreMode
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class HasChildBodyFnTest extends AnyFunSuite with Matchers {

  test("has child should generate expected json") {
    val q = HasChildQuery("blog_tag", MatchQuery("tag", "something"), ScoreMode.Min)
      .boost(1.2)
      .minMaxChildren(2, 10)
      .ignoreUnmapped(true)
      .queryName("myquery")
      .innerHit(InnerHit("inners"))
    HasChildBodyFn(q).string() shouldBe
      """{"has_child":{"type":"blog_tag","min_children":2,"max_children":10,"score_mode":"min","query":{"match":{"tag":{"query":"something"}}},"ignore_unmapped":true,"boost":1.2,"_name":"myquery","inner_hits":{"name":"inners"}}}"""
  }
}
