package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class LimitTokenCountTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "LimitTokenCountTokenFilter builder" should {
    "not set any defaults" in {
      limitTokenCountTokenFilter("testy").json.string shouldBe """{"type":"limit"}"""
    }
    "set max token count" in {
      limitTokenCountTokenFilter("testy").maxTokenCount(7).json.string shouldBe """{"type":"limit","max_token_count":7}"""
    }
    "set consume all tokens" in {
      limitTokenCountTokenFilter("testy").consumeAllTokens(true).json.string shouldBe """{"type":"limit","consume_all_tokens":true}"""
    }
  }
}
