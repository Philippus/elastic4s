package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class KeywordTokenizerTest extends WordSpec with TokenizerApi with Matchers {

  "KeywordTokenizer builder" should {
    "set buffer size" in {
      keywordTokenizer("testy").bufferSize(123).json.string shouldBe """{"type":"keyword","bufferSize":123}"""
    }
  }
}
