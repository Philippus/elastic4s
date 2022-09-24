package com.sksamuel.elastic4s.analysis.analyzers

import com.sksamuel.elastic4s.analysis.PatternAnalyzer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PatternAnalyzerBuilderTest extends AnyWordSpec with Matchers {

  "PatternAnalyzer" should {
    "build json" in {
      PatternAnalyzer("testy", regex = "21.*").lowercase(true).build.string shouldBe """{"type":"pattern","lowercase":true,"pattern":"21.*"}"""
    }
  }
}
