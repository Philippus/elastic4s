package com.sksamuel.elastic4s.analysis

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CustomAnalyzerBuilderTest extends AnyFunSuite with Matchers {

  test("custom analyzer builder with all standard options") {
    val a = CustomAnalyzer("my_custom_analyzer", "uaxurl", Nil, Nil)
    CustomAnalyzerBuilder.build(a).string() shouldBe """{"type":"custom","tokenizer":"uaxurl"}"""
  }

}
