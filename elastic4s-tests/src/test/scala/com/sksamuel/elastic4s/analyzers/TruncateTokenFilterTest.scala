package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class TruncateTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "TruncateTokenFilter builder" should {
    "not set any defaults" in {
      truncateTokenFilter("testy").json.string shouldBe """{"type":"truncate"}"""
    }
  }
}
