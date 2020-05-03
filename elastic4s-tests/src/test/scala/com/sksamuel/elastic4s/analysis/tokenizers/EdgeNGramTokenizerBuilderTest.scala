package com.sksamuel.elastic4s.analysis.tokenizers

import com.sksamuel.elastic4s.analysis.EdgeNGramTokenizer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EdgeNGramTokenizerBuilderTest extends AnyWordSpec with Matchers {

  "EdgeNGramTokenizer" should {
    "build json" in {
      EdgeNGramTokenizer("testy").minMaxGrams(2, 3).tokenChars("a", "z").build.string shouldBe """{"type":"edgeNGram","min_gram":2,"max_gram":3,"token_chars":["a","z"]}"""
    }
  }
}
