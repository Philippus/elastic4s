package com.sksamuel.elastic4s.requests.analysis

import org.scalatest.{FunSuite, Matchers}

class NormalizerBuilderTest extends FunSuite with Matchers {

  test("should build normalizer to spec") {
    val n = CustomNormalizer("my_normalizer", List("quote"), List("lowercase", "asciifolding"))
    NormalizerBuilder.build(n).string() shouldBe """{"normalizer":{"my_normalizer":{"type":"custom","filter":["lowercase","asciifolding"],"char_filter":["quote"]}}}"""
  }
}
