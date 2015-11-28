package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class ElisionTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "ElisionTokenFilter builder" should {
    "set articles" in {
      elisionTokenFilter("testy")
        .articles("a", "b")
        .json
        .string shouldBe
        """{"type":"elision","articles":["a","b"]}"""
    }
  }
}
