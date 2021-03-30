package com.sksamuel.elastic4s.requests.analyzers

import com.sksamuel.elastic4s.api.TokenizerApi
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EdgeNGramTokenizerTest extends AnyWordSpec with TokenizerApi with Matchers {

  "EdgeNGramTokenizer builder" should {
    "set min and max ngrams" in {
      edgeNGramTokenizer("testy").minMaxGrams(3, 4).json.string shouldBe """{"type":"edgeNGram","min_gram":3,"max_gram":4}"""
    }
    "set token chars" in {
      edgeNGramTokenizer("testy")
        .tokenChars("a", "b")
        .json
        .string shouldBe """{"type":"edgeNGram","min_gram":1,"max_gram":2,"token_chars":["a","b"]}"""
    }
    "not set token chars if not specified" in {
      edgeNGramTokenizer("testy").json.string shouldBe """{"type":"edgeNGram","min_gram":1,"max_gram":2}"""
    }
  }
}
