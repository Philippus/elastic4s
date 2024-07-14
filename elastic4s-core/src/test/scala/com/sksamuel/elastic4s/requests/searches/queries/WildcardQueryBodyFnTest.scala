package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.searches.queries.term
import com.sksamuel.elastic4s.requests.searches.term.WildcardQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

// Test of https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-wildcard-query.html
class WildcardQueryBodyFnTest extends AnyFunSuite with Matchers {
  test("Wildcard query should generate expected json") {
    val q = WildcardQuery("myfield.wildcard", "abc*")
    term.WildcardQueryBodyFn(q).string shouldBe
      """{"wildcard":{"myfield.wildcard":{"value":"abc*"}}}"""
  }

  test("Case insensitive Wildcard query should generate expected json") {
    val q = WildcardQuery("myfield.wildcard", "abc*")
      .caseInsensitive(true)
    term.WildcardQueryBodyFn(q).string shouldBe
      """{"wildcard":{"myfield.wildcard":{"value":"abc*","case_insensitive":true}}}"""
  }

  test("Case sensitive Wildcard query should generate expected json") {
    val q = WildcardQuery("myfield.wildcard", "abc*")
      .caseInsensitive(false)
    term.WildcardQueryBodyFn(q).string shouldBe
      """{"wildcard":{"myfield.wildcard":{"value":"abc*","case_insensitive":false}}}"""
  }
}
