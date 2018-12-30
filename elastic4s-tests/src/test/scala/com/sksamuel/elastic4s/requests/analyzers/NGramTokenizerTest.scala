package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class NGramTokenizerTest extends WordSpec with TokenizerApi with Matchers {

  "NGramTokenizer builder" should {
    "set min and max ngrams" in {
      nGramTokenizer("testy").minMaxGrams(3, 4).json.string shouldBe """{"type":"nGram","min_gram":3,"max_gram":4}"""
    }
    "set token chars" in {
      nGramTokenizer("testy")
        .tokenChars("a", "b")
        .json
        .string shouldBe """{"type":"nGram","min_gram":1,"max_gram":2,"token_chars":["a","b"]}"""
    }
    "not set token chars if not specified" in {
      nGramTokenizer("testy").json.string shouldBe """{"type":"nGram","min_gram":1,"max_gram":2}"""
    }
  }
}
