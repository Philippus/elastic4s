package com.sksamuel.elastic4s.analyzers

import com.sksamuel.elastic4s.anaylzers.{TokenizerDsl, AnalyzerDsl}
import org.scalatest.{Matchers, WordSpec}

class KeywordTokenizerTest extends WordSpec with TokenizerDsl with Matchers {

  "KeywordTokenizer builder" should {
    "set buffer size" in {
      keywordTokenizer("testy").bufferSize(123).json.string shouldBe """{"type":"keyword","bufferSize":123}"""
    }
  }
}
