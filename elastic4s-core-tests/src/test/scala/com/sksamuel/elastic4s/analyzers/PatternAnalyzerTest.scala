package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.analyzers.AnalyzerDsl
import org.scalatest.{Matchers, WordSpec}

class PatternAnalyzerTest extends WordSpec with AnalyzerDsl with Matchers {

  "PatternAnalyzer builder" should {
    "set language" in {
      snowballAnalyzer("testy")
        .language("klingon")
        .json
        .string shouldBe """{"type":"snowball","language":"klingon"}"""
    }
    "set stopwords" in {
      snowballAnalyzer("testy")
        .stopwords("a", "b")
        .json
        .string shouldBe """{"type":"snowball","language":"English","stopwords":["a","b"]}"""
    }
    "not set stopwords if not specified" in {
      snowballAnalyzer("testy").json.string shouldBe """{"type":"snowball","language":"English"}"""
    }
  }
}
