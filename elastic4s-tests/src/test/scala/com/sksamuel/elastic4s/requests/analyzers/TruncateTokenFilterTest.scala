package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class TruncateTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "TruncateTokenFilter builder" should {
    "not set any defaults" in {
      truncateTokenFilter("testy").json.string shouldBe """{"type":"truncate"}"""
    }
    "set length" in {
      truncateTokenFilter("testy").length(5).json.string shouldBe """{"type":"truncate","length":5}"""
    }
  }
}
