package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.anaylzers.TokenFilterDsl
import org.scalatest.{Matchers, WordSpec}

class EdgeNGramTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "EdgeNGramTokenFilter builder" should {
    "set min and max ngrams" in {
      edgeNGramTokenFilter("testy")
        .minMaxGrams(3, 4)
        .json
        .string shouldBe """{"type":"edgeNGram","min_gram":3,"max_gram":4,"side":"front"}"""
    }
    "set token chars" in {
      edgeNGramTokenFilter("testy")
        .side("backside")
        .json
        .string shouldBe """{"type":"edgeNGram","min_gram":1,"max_gram":2,"side":"backside"}"""
    }
  }
}
