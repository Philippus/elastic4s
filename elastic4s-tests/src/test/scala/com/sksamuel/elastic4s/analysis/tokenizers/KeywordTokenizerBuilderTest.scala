package com.sksamuel.elastic4s.analysis.tokenizers

import com.sksamuel.elastic4s.analysis.KeywordTokenizer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class KeywordTokenizerBuilderTest extends AnyWordSpec with Matchers {

  "KeywordTokenizer" should {
    "build json" in {
      KeywordTokenizer("testy").bufferSize(123).build.string shouldBe """{"type":"keyword","bufferSize":123}"""
    }
  }
}
