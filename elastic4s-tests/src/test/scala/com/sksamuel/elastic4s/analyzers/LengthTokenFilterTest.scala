package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class LengthTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "LengthTokenFilter builder" should {
    "not set defaults" in {
      lengthTokenFilter("testy").json.string shouldBe """{"type":"length"}"""
    }
    "set min" in {
      lengthTokenFilter("testy").min(2).json.string shouldBe """{"type":"length","min":2}"""
    }
    "set max" in {
      lengthTokenFilter("testy").max(55).json.string shouldBe """{"type":"length","max":55}"""
    }
  }
}
