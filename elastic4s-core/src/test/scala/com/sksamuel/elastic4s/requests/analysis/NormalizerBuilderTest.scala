package com.sksamuel.elastic4s.requests.analysis

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class NormalizerBuilderTest extends AnyFunSuite with Matchers {

  test("should build normalizer to spec") {
    val n = CustomNormalizer("my_normalizer", List("quote"), List("lowercase", "asciifolding"))
    NormalizerBuilder.build(n).string() shouldBe """{"type":"custom","filter":["lowercase","asciifolding"],"char_filter":["quote"]}"""
  }
}
