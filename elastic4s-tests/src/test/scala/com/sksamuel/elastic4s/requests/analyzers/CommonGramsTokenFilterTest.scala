package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class CommonGramsTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "CommonGramsTokenFilter builder" should {
    "not set any defaults" in {
      commonGramsTokenFilter("testy").json.string shouldBe """{"type":"common_grams"}"""
    }
    "set common words" in {
      commonGramsTokenFilter("testy")
        .commonWords("the", "and")
        .json
        .string shouldBe """{"type":"common_grams","common_words":["the","and"]}"""
    }
    "set common words path" in {
      commonGramsTokenFilter("testy")
        .commonWordsPath("some/file.txt")
        .json
        .string shouldBe """{"type":"common_grams","common_words_path":"some/file.txt"}"""
    }
    "set ignore case" in {
      commonGramsTokenFilter("testy")
        .ignoreCase(true)
        .json
        .string shouldBe """{"type":"common_grams","ignore_case":true}"""
    }
    "set query mode" in {
      commonGramsTokenFilter("testy")
        .queryMode(true)
        .json
        .string shouldBe """{"type":"common_grams","query_mode":true}"""
    }
  }
}
