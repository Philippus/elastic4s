package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class StandardTokenizerTest extends WordSpec with TokenizerApi with Matchers {

  "StandardTokenizer builder" should {
    "set max token length" in {
      standardTokenizer("testy").maxTokenLength(14).json.string shouldBe """{"type":"standard","max_token_length":14}"""
    }
  }
}
