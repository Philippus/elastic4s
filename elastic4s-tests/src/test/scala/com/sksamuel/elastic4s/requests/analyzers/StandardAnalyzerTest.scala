package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.AnalyzerApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StandardAnalyzerTest extends AnyWordSpec with AnalyzerApi with Matchers {

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
