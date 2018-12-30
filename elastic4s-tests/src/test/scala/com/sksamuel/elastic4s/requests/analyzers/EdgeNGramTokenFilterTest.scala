package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class EdgeNGramTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "EdgeNGramTokenFilter builder" should {
    "not set any defaults" in {
      edgeNGramTokenFilter("testy").json.string shouldBe """{"type":"edgeNGram"}"""
    }
    "set min and max ngrams" in {
      edgeNGramTokenFilter("testy")
        .minMaxGrams(3, 4)
        .json
        .string shouldBe """{"type":"edgeNGram","min_gram":3,"max_gram":4}"""
    }
    "set token chars" in {
      edgeNGramTokenFilter("testy")
        .side("backside")
        .json
        .string shouldBe """{"type":"edgeNGram","side":"backside"}"""
    }
  }
}
