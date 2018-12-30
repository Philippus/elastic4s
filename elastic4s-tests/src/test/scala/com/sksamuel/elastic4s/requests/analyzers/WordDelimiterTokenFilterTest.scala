package com.sksamuel.elastic4s.requests.analyzers

import org.scalatest.{Matchers, WordSpec}

class WordDelimiterTokenFilterTest extends WordSpec with TokenFilterApi with Matchers {

  "WordDelimiterTokenFilter builder" should {
    "set generateNumberParts" in {
      wordDelimiterTokenFilter("testy")
        .generateNumberParts(true)
        .json
        .string shouldBe """{"type":"word_delimiter","generate_number_parts":true}"""
    }
    "set generateWordParts" in {
      wordDelimiterTokenFilter("testy")
        .generateWordParts(true)
        .json
        .string shouldBe """{"type":"word_delimiter","generate_word_parts":true}"""
    }
    "set splitOnCaseChange" in {
      wordDelimiterTokenFilter("testy")
        .splitOnCaseChange(true)
        .json
        .string shouldBe """{"type":"word_delimiter","split_on_case_change":true}"""
    }
    "set splitOnNumerics" in {
      wordDelimiterTokenFilter("testy")
        .splitOnNumerics(true)
        .json
        .string shouldBe """{"type":"word_delimiter","split_on_numerics":true}"""
    }
    "set stemEnglishPossesive" in {
      wordDelimiterTokenFilter("testy")
        .stemEnglishPossesive(true)
        .json
        .string shouldBe """{"type":"word_delimiter","stem_english_possessive":true}"""
    }
    "set catenateAll" in {
      wordDelimiterTokenFilter("testy")
        .catenateAll(true)
        .json
        .string shouldBe """{"type":"word_delimiter","catenate_all":true}"""
    }
    "set catenateNumbers" in {
      wordDelimiterTokenFilter("testy")
        .catenateNumbers(true)
        .json
        .string shouldBe """{"type":"word_delimiter","catenate_numbers":true}"""
    }
    "set catenateWords" in {
      wordDelimiterTokenFilter("testy")
        .catenateWords(true)
        .json
        .string shouldBe """{"type":"word_delimiter","catenate_words":true}"""
    }
    "set preserveOriginal" in {
      wordDelimiterTokenFilter("testy")
        .preserveOriginal(true)
        .json
        .string shouldBe """{"type":"word_delimiter","preserve_original":true}"""
    }
  }
}
