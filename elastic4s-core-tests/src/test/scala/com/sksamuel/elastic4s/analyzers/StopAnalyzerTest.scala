package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.analyzers.AnalyzerDsl
import org.scalatest.{Matchers, WordSpec}

class StopAnalyzerTest extends WordSpec with AnalyzerDsl with Matchers {

  "StopAnalyzer builder" should {
    "set stopwords" in {
      stopAnalyzer("testy").stopwords("a", "b").json.string shouldBe """{"type":"stop","stopwords":["a","b"]}"""
    }
  }
}
