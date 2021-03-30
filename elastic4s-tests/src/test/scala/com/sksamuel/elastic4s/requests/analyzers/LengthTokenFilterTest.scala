package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LengthTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

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
