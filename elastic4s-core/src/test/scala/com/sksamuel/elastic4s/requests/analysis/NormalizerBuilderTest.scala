package com.sksamuel.elastic4s.requests.analysis

import org.scalatest.{FunSuite, Matchers}

class NormalizerBuilderTest extends FunSuite with Matchers {

  test("should build normalizer to spec") {
    val n = Normalizer("my_normalizer", List(CharFilter("quote")), List(TokenFilter("lowercase"), TokenFilter("asciifolding")))
    NormalizerBuilder.build(n).string() shouldBe """{"normalizer":{"my_normalizer":{"type":"custom","filter":["lowercase","asciifolding"],"char_filter":["quote"]}}}"""
  }
}
