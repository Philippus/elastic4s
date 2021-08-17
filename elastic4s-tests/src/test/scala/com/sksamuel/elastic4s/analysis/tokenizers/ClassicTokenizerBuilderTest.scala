package com.sksamuel.elastic4s.analysis.tokenizers

import com.sksamuel.elastic4s.analysis.ClassicTokenizer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ClassicTokenizerBuilderTest extends AnyWordSpec with Matchers {

  "ClassicTokenizer" should {
    "build json" in {
      ClassicTokenizer("testy", 123).build.string() shouldBe """{"type":"classic","max_token_length":123}"""
    }
  }
}
