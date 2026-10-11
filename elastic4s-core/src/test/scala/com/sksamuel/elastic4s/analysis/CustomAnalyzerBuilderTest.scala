package com.sksamuel.elastic4s.analysis

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CustomAnalyzerBuilderTest extends AnyFunSuite with Matchers {

  test("custom analyzer builder with all standard options") {
    val a = CustomAnalyzer("my_custom_analyzer", "uaxurl", Nil, Nil)
    CustomAnalyzerBuilder.build(a).string shouldBe """{"type":"custom","tokenizer":"uaxurl"}"""
  }

  test("custom analyzer builder with position increment gap") {
    val a = CustomAnalyzer("my_custom_analyzer", "standard", positionIncrementGap = 50)
    CustomAnalyzerBuilder.build(a).string shouldBe
      """{"type":"custom","tokenizer":"standard","position_increment_gap":50}"""
  }

  test("custom analyzer builder omits the default position increment gap") {
    val a = CustomAnalyzer("my_custom_analyzer", "standard", positionIncrementGap = 100)
    CustomAnalyzerBuilder.build(a).string shouldBe """{"type":"custom","tokenizer":"standard"}"""
  }

}
