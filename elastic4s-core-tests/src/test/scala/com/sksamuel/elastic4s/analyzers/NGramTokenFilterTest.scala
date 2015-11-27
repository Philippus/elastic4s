package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.analyzers.TokenFilterDsl
import org.scalatest.{Matchers, WordSpec}

class NGramTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "NGramTokenFilter builder" should {
    "set min and max ngrams" in {
      ngramTokenFilter("testy").minMaxGrams(3, 4).json.string shouldBe """{"type":"nGram","min_gram":3,"max_gram":4}"""
    }
  }
}
