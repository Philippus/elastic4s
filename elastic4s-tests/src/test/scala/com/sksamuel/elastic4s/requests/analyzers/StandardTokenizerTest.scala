package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenizerApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StandardTokenizerTest extends AnyWordSpec with TokenizerApi with Matchers {

  "StandardTokenizer builder" should {
    "set max token length" in {
      standardTokenizer("testy").maxTokenLength(14).json.string() shouldBe """{"type":"standard","max_token_length":14}"""
    }
  }
}
