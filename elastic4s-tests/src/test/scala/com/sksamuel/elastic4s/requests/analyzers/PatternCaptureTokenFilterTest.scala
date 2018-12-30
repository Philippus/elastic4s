package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class PatternCaptureTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "PatternAnalyzer builder" should {
    "set patterns" in {
      patternCaptureTokenFilter("testy")
        .patterns("a", "b")
        .json
        .string shouldBe
        """{"type":"pattern_capture","patterns":["a","b"],"preserve_original":true}"""
    }
    "set preserveOriginal" in {
      patternCaptureTokenFilter("testy")
        .preserveOriginal(false)
        .json
        .string shouldBe
        """{"type":"pattern_capture","preserve_original":false}"""
    }
    "not set patterns if not specified" in {
      patternCaptureTokenFilter("testy").json.string shouldBe """{"type":"pattern_capture","preserve_original":true}"""
    }
  }
}
