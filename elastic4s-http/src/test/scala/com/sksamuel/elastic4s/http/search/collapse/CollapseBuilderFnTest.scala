package com.sksamuel.elastic4s.http.search.collapse

import com.sksamuel.elastic4s.searches.collapse.CollapseDefinition
import com.sksamuel.elastic4s.searches.queries.InnerHitDefinition
import org.scalatest.{FunSuite, Matchers}

class CollapseBuilderFnTest extends FunSuite with Matchers {

  test("collapse builder should generate simple collapse json") {
    val c = CollapseDefinition("something")
    CollapseBuilderFn.apply(c).string shouldBe """{"field":"something"}"""
  }

  test("collapse builder should support inner hits and max searches") {
    val c = CollapseDefinition("something")
      .inner(InnerHitDefinition("name").size(1))
      .maxConcurrentGroupSearches(8)
    CollapseBuilderFn.apply(c).string shouldBe
      """{"field":"something","max_concurrent_group_searches":8,"inner_hits":{"size":1}}"""
  }
}
