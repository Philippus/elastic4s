package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.JsonSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ExtBuilderFnTest extends AnyFunSuite with Matchers with JsonSugar {

  test("serialize ext in the search request properly") {
    val search = SearchRequest("anyIndex").ext(Map(
      "custom" -> Map(
        "a_string_property" -> "string property",
        "a_long_property" -> 1L,
        "nested_map" -> Map (
          "another_string_property" -> "string property",
          "another_long_property" -> 2L
        )
      )
    ))
    SearchBodyBuilderFn(search).string shouldBe
      """{"ext":{"custom":{"a_string_property":"string property","a_long_property":1,"nested_map":{"another_string_property":"string property","another_long_property":2}}}}"""
  }

  test("not serialize ext in the search request when it is empty") {
    val search = SearchRequest("anyIndex").ext(Map.empty[String, Any])
    SearchBodyBuilderFn(search).string shouldBe """{}"""
  }
}
