package com.sksamuel.elastic4s.analysis.tokenizers

import com.sksamuel.elastic4s.analysis.WhitespaceTokenizer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class WhitespaceTokenizerBuilderTest extends AnyWordSpec with Matchers {

  "WhitespaceTokenizer" should {
    "build json" in {
      WhitespaceTokenizer("testy", 123).build.string shouldBe """{"type":"whitespace","max_token_length":123}"""
    }
  }
}
