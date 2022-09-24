package com.sksamuel.elastic4s.requests.searches.collapse

import com.sksamuel.elastic4s.handlers.searches.collapse.CollapseBuilderFn
import com.sksamuel.elastic4s.requests.searches.queries.InnerHit
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CollapseBuilderFnTest extends AnyFunSuite with Matchers {

  test("collapse builder should generate simple collapse json") {
    val c = CollapseRequest("something")
    CollapseBuilderFn.apply(c).string shouldBe """{"field":"something"}"""
  }

  test("collapse builder should support inner hits and max searches") {
    val c = CollapseRequest("something")
      .inner(InnerHit("name").size(1))
      .maxConcurrentGroupSearches(8)
    CollapseBuilderFn.apply(c).string shouldBe
      """{"field":"something","max_concurrent_group_searches":8,"inner_hits":{"name":"name","size":1}}"""
  }
}
