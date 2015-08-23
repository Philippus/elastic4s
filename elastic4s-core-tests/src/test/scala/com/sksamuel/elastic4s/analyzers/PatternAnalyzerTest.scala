package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.anaylzers.AnalyzerDsl
import org.scalatest.{Matchers, WordSpec}

class PatternAnalyzerTest extends WordSpec with AnalyzerDsl with Matchers {

  "PatternAnalyzer builder" should {
    "set regex" in {
      patternAnalyzer("testy", "\\d")
        .json
        .string shouldBe """{"type":"pattern","lowercase":true,"pattern":"\\d"}"""
    }
    "set lowercase" in {
      patternAnalyzer("testy", "\\s")
        .lowercase(true)
        .json
        .string shouldBe """{"type":"pattern","lowercase":true,"pattern":"\\s"}"""
    }
  }
}
