package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class NGramTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "NGramTokenFilter builder" should {
    "not set any defaults" in {
      ngramTokenFilter("testy").json.string shouldBe """{"type":"nGram"}"""
    }
    "set min and max ngrams" in {
      ngramTokenFilter("testy").minMaxGrams(3, 4).json.string shouldBe """{"type":"nGram","min_gram":3,"max_gram":4}"""
    }
  }
}
