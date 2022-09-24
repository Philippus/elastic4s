package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TruncateTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

  "TruncateTokenFilter builder" should {
    "not set any defaults" in {
      truncateTokenFilter("testy").json.string shouldBe """{"type":"truncate"}"""
    }
    "set length" in {
      truncateTokenFilter("testy").length(5).json.string shouldBe """{"type":"truncate","length":5}"""
    }
  }
}
