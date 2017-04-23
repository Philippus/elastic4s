package com.sksamuel.elastic4s.http.search.queries

import org.scalatest.{FunSuite, Matchers}
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.search.SearchBodyBuilderFn

class SearchBodyFnTest extends FunSuite with Matchers {

  test("highlight with 'matchedMatchedFields' generates proper 'matched_fields' field as array field.") {
    val request = search("example" / "1") highlighting {
      highlight("text")
      .matchedFields("text", "text.ngram", "text.japanese")
    }
    SearchBodyBuilderFn(request).string() shouldBe
      """{"highlight":{"fields":{"text":{"matched_fields":["text","text.ngram","text.japanese"]}}}}"""
  }
  test("highlight with 'highlighterType' generates 'type' field.") {
    val request = search("example" / "1") highlighting {
      highlight("text")
        .highlighterType("fvh")
    }
    SearchBodyBuilderFn(request).string() shouldBe
      """{"highlight":{"fields":{"text":{"type":"fvh"}}}}"""
  }
  test("highlight with 'boundaryChars' generates 'boundary_chars' field.") {
    val request = search("example" / "1") highlighting {
      highlight("text")
        .boundaryChars("test")
    }
    SearchBodyBuilderFn(request).string() shouldBe
      """{"highlight":{"fields":{"text":{"boundary_chars":"test"}}}}"""
  }
}
