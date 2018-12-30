package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.search.queries.nested.HasParentBodyFn
import com.sksamuel.elastic4s.searches.queries.{HasParentQuery, InnerHit}
import com.sksamuel.elastic4s.searches.queries.matches.MatchQuery
import org.scalatest.{FunSuite, Matchers}

class HasParentBodyFnTest extends FunSuite with Matchers {

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
