package com.sksamuel.elastic4s.requests.analysis

import org.scalatest.{FunSuite, Matchers}

class CustomAnalyzerBuilderTest extends FunSuite with Matchers {

  test("custom analyzer builder with all standard options") {
    val a = CustomAnalyzer("my_custom_analyzer", "uaxurl", Nil, Nil)
    CustomAnalyzerBuilder.build(a).string() shouldBe """{"type":"custom","tokenizer":"uaxurl"}"""
  }

}
