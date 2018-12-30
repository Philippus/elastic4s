package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class UaxUrlEmailTokenizerTest extends WordSpec with TokenizerApi with Matchers {

  "UaxUrlEmailTokenizer builder" should {
    "set max token length" in {
      uaxUrlEmailTokenizer("testy")
        .maxTokenLength(14)
        .json
        .string shouldBe """{"type":"uax_url_email","max_token_length":14}"""
    }
  }
}
