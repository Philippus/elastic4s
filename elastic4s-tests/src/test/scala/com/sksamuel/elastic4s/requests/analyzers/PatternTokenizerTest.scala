package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class PatternTokenizerTest extends WordSpec with TokenizerApi with Matchers {

  "PatternTokenizer builder" should {
    "set flags" in {
      patternTokenizer("testy").flags("abc").json.string shouldBe """{"type":"pattern","flags":"abc","pattern":"\\W+"}"""
    }
    "not set flags if not specified" in {
      patternTokenizer("testy").json.string shouldBe """{"type":"pattern","pattern":"\\W+"}"""
    }
    "set pattern" in {
      patternTokenizer("testy")
        .pattern("aRRgh")
        .json
        .string shouldBe """{"type":"pattern","pattern":"aRRgh"}"""
    }
    "set group if > 0" in {
      patternTokenizer("testy").group(3).json.string shouldBe """{"type":"pattern","pattern":"\\W+","group":3}"""
    }
  }
}
