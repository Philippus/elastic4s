package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class StopAnalyzerTest extends WordSpec with AnalyzerApi with Matchers {

  "StopAnalyzer builder" should {
    "set stopwords" in {
      stopAnalyzer("testy").stopwords("a", "b").json.string shouldBe """{"type":"stop","stopwords":["a","b"]}"""
    }
  }
}
