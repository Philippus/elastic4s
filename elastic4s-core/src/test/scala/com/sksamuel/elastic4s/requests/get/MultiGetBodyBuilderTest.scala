package com.sksamuel.elastic4s.requests.get

import com.sksamuel.elastic4s.ElasticDsl.*
import com.sksamuel.elastic4s.handlers.get.MultiGetBodyBuilder
import com.sksamuel.elastic4s.requests.common.FetchSourceContext
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MultiGetBodyBuilderTest extends AnyFunSuite with Matchers {

  test("multiget body builder should leave _source out when fetchSource is not set") {
    MultiGetBodyBuilder(multiget(get("coldplay", "3"))).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3"}]}"""
  }

  test("multiget body builder should enable _source when fetchSource is true") {
    MultiGetBodyBuilder(multiget(get("coldplay", "3").fetchSourceContext(true))).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3","_source":true}]}"""
  }

  test("multiget body builder should disable _source when fetchSource is false") {
    MultiGetBodyBuilder(multiget(get("coldplay", "3").fetchSourceContext(false))).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3","_source":false}]}"""
  }

  test("multiget body builder should support source includes") {
    MultiGetBodyBuilder(multiget(get("coldplay", "3").fetchSourceInclude("name"))).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3","_source":{"includes":["name"]}}]}"""
  }

  test("multiget body builder should support source excludes") {
    MultiGetBodyBuilder(multiget(get("coldplay", "3").fetchSourceExclude("year"))).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3","_source":{"excludes":["year"]}}]}"""
  }

  test("multiget body builder should support source includes and excludes") {
    MultiGetBodyBuilder(
      multiget(get("coldplay", "3").fetchSourceContext(FetchSourceContext(true, Set("name"), Set("year"))))
    ).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3","_source":{"includes":["name"],"excludes":["year"]}}]}"""
  }

  test("multiget body builder should disable _source when fetchSource is false even with includes") {
    MultiGetBodyBuilder(
      multiget(get("coldplay", "3").fetchSourceContext(FetchSourceContext(false, Set("name"))))
    ).string shouldBe
      """{"docs":[{"_index":"coldplay","_id":"3","_source":false}]}"""
  }
}
