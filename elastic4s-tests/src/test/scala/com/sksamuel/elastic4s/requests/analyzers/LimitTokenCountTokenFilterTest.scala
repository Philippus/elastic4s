package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenFilterApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LimitTokenCountTokenFilterTest extends AnyWordSpec with TokenFilterApi with Matchers {

  "LimitTokenCountTokenFilter builder" should {
    "not set any defaults" in {
      limitTokenCountTokenFilter("testy").json.string() shouldBe """{"type":"limit"}"""
    }
    "set max token count" in {
      limitTokenCountTokenFilter("testy").maxTokenCount(7).json.string() shouldBe """{"type":"limit","max_token_count":7}"""
    }
    "set consume all tokens" in {
      limitTokenCountTokenFilter("testy").consumeAllTokens(true).json.string() shouldBe """{"type":"limit","consume_all_tokens":true}"""
    }
  }
}
