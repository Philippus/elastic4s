package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.requests.searches.ScoreMode
import com.sksamuel.elastic4s.requests.searches.queries.{HasChildQuery, InnerHit}
import com.sksamuel.elastic4s.requests.searches.queries.matches.MatchQuery
import com.sksamuel.elastic4s.requests.searches.queries.nested.HasChildBodyFn
import org.scalatest.{FunSuite, Matchers}

class HasChildBodyFnTest extends FunSuite with Matchers {

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
