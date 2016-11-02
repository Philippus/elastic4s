package com.sksamuel.elastic4s.analyzers

import org.scalatest.{Matchers, WordSpec}

class CommonGramsTokenFilterTest extends WordSpec with TokenFilterDsl with Matchers {

  "CommonGramsTokenFilter builder" should {
    "set common words" in {
      commonGramsTokenFilter("testy")
        .commonWords("the", "and")
        .json
        .string shouldBe """{"type":"common_grams","common_words":["the","and"],"ignore_case":false,"query_mode":false}"""
    }
    "set ignore case" in {
      commonGramsTokenFilter("testy")
        .ignoreCase(true)
        .json
        .string shouldBe """{"type":"common_grams","ignore_case":true,"query_mode":false}"""
    }
    "set query mode" in {
      commonGramsTokenFilter("testy")
        .queryMode(true)
        .json
        .string shouldBe """{"type":"common_grams","ignore_case":false,"query_mode":true}"""
    }
  }
}
