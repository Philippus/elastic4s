package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.HasParentQueryDefinition
import com.sksamuel.elastic4s.searches.queries.matches.MatchQueryDefinition
import org.scalatest.{FunSuite, Matchers}

class HasParentBodyFnTest extends FunSuite with Matchers {

  test("has parent should generate expected json") {
    val q = HasParentQueryDefinition("blog", MatchQueryDefinition("tag", "something"), true)
      .boost(1.2)
      .ignoreUnmapped(true)
      .queryName("myquery")
    HasParentBodyFn(q).string() shouldBe
      """{"has_parent":{"parent_type":"blog","query":{"match":{"tag":{"query":"something"}}},"ignore_unmapped":true,"score":true,"boost":1.2,"_name":"myquery"}}"""
  }
}
