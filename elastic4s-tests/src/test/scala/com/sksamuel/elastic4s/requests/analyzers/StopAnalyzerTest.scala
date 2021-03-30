package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.AnalyzerApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StopAnalyzerTest extends AnyWordSpec with AnalyzerApi with Matchers {

  "StopAnalyzer builder" should {
    "set stopwords" in {
      stopAnalyzer("testy").stopwords("a", "b").json.string shouldBe """{"type":"stop","stopwords":["a","b"]}"""
    }
  }
}
