package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ElisionTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

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
