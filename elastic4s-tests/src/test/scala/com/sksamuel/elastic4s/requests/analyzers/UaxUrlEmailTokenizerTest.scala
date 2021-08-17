package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenizerApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UaxUrlEmailTokenizerTest extends AnyWordSpec with TokenizerApi with Matchers {

  "UaxUrlEmailTokenizer builder" should {
    "set max token length" in {
      uaxUrlEmailTokenizer("testy")
        .maxTokenLength(14)
        .json
        .string() shouldBe """{"type":"uax_url_email","max_token_length":14}"""
    }
  }
}
