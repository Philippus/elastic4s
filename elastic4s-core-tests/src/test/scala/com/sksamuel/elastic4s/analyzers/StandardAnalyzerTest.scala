package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.analyzers.AnalyzerDsl
import org.scalatest.{Matchers, WordSpec}

class StandardAnalyzerTest extends WordSpec with AnalyzerDsl with Matchers {

  "StandardAnalyzer builder" should {
    "set stopwords" in {
      standardAnalyzer("testy")
        .stopwords("a", "b")
        .json
        .string shouldBe """{"type":"standard","stopwords":["a","b"],"max_token_length":255}"""
    }
    "set maxTokenLength" in {
      standardAnalyzer("testy")
        .maxTokenLength(34)
        .json
        .string shouldBe """{"type":"standard","stopwords":[],"max_token_length":34}"""
    }
  }
}
